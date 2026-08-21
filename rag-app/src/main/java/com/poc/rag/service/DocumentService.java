package com.poc.rag.service;

import com.poc.rag.entity.Document;
import com.poc.rag.entity.DocumentChunk;
import com.poc.rag.exception.DuplicateFileException;
import com.poc.rag.repository.ChunkRepository;
import com.poc.rag.repository.DocumentRepository;
import com.poc.rag.util.HashUtil;
import com.poc.rag.util.RecursiveTextChunker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.apache.pdfbox.Loader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final EmbeddingService embeddingService;

    private final JdbcTemplate jdbcTemplate;

    private final RecursiveTextChunker recursiveTextChunker;

    private final DocumentRepository documentRepository;

    private final ChunkRepository chunkRepository;


    public void ingestDocumentToRAG(MultipartFile file) throws Exception {
        String text = validateAndExtractText(file);
        isDuplicateContent(text, file.getOriginalFilename());
        Long documentId = saveDocument(file, text);
        saveDocumentChunks(text, documentId);
    }


    private void saveDocumentChunks(String text, Long documentId) {
        List<String> chunks = recursiveTextChunker.chunk(text);

        for(int chunkIndex = 0; chunkIndex < chunks.size(); chunkIndex++) {
            String chunk = chunks.get(chunkIndex);
            float[] vector = embeddingService.embed(chunk);
            String vectorString = Arrays.toString(vector);
            save(documentId, chunkIndex, chunk, vectorString);
        }
    }

    private Long saveDocument(MultipartFile file, String text) {
        String hashValue = HashUtil.sha256(text);
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setFileHash(hashValue);

        Document result = documentRepository.save(document);
        log.info("Inserted Document detail :{}", result);

        return result.getDocumentId();
    }

    private String validateAndExtractText(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if ("application/pdf".equals(contentType)) {
            return readPdf(file);
        }
        else if ("text/plain".equals(contentType)) {
            return new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
        else {
            throw new IllegalArgumentException("Unsupported file type. Only PDF and TXT files are supported");
        }
    }


    private void isDuplicateContent(String text, @Nullable String originalFilename) {
        String newHash = HashUtil.sha256(text);

        Optional<Document> result = documentRepository.findByFileHash(newHash);
        log.info("Duplicate file document : {}", result);
        if (result.isPresent()) {
            throw new DuplicateFileException("File content already existing in our system : " + originalFilename);
        }

    }


    public String readPdf(MultipartFile file) throws IOException {

        try (PDDocument doc = Loader.loadPDF(
                file.getBytes())) {

            return new PDFTextStripper().getText(doc);
        }
    }

    public List<String> chunkText(String text) {
        int chunkSize = 1000;
        int overlap = 200;

        List<String> chunks = new ArrayList<>();
        int start = 0;

        while(start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start,end));
            start += chunkSize-overlap;
        }

        return chunks;
    }

    private void save(Long documentId, Integer chunkIndex, String chunk, String vectorString) {

        DocumentChunk documentChunk = new DocumentChunk();
        documentChunk.setDocumentId(documentId);
        documentChunk.setChunkIndex(chunkIndex);
        documentChunk.setChunkText(chunk);
        documentChunk.setEmbedding(vectorString);

        chunkRepository.save(documentChunk);

    }
}
