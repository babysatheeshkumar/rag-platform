package com.poc.rag.service;

import com.anthropic.errors.BadRequestException;
import com.poc.rag.dto.AskResponse;
import com.poc.rag.exception.PromptTooLongException;
import com.poc.rag.prompt.PromptRenderer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {

    // pgvector's "<=>" operator returns COSINE DISTANCE (0 = identical, 2 = opposite).
    // We fetch a few extra candidates beyond top-K so the actual distance/similarity
    // of near-misses is visible in the logs, which makes it much easier to tune the
    // threshold below against your own documents.
    private static final int CANDIDATE_POOL_SIZE = 8;

    @Value("${app.llm.active-model}")
    private String model;

    @Value("${app.input-token-limit:1000000}")
    private int maxInputToken; // for the model claude-sonnet-5

    @Value("${app.table-name:document_chunks}")
    private String tableName;

    // Minimum cosine similarity (1 - cosine distance) a chunk must have to be treated
    // as relevant. Anything below this is dropped instead of being sent to the LLM,
    // which is what keeps the model from being handed unrelated context and
    // hallucinating an answer from it.
    @Value("${app.similarity-threshold:0.70}")
    private double similarityThreshold;

    // Max number of chunks actually sent to the chat model as context.
    @Value("${app.top-k:3}")
    private int topK;

    private final EmbeddingService embeddingService;
    private final JdbcTemplate jdbcTemplate;
    private final ChatClient chatClient;
    private final TokenCountEstimator tokenCountEstimator;
    private final RagAuditService ragAuditService;
    private final PromptRenderer promptRenderer;


    public AskResponse ask(String question) {

        log.info("Question : {}", question);
        validateQuestion(question);

        String embeddingVector = getEmbeddingVector(question);

        List<Map<String, Object>> rows = retrieveRelevantChunks(embeddingVector);

        if(rows.isEmpty()) {
            log.info("No chunk met the similarity threshold {} for question: {}", similarityThreshold, question);
            return new AskResponse("I couldn't find anything relevant to that question in the document.", null);
        }


        StringBuilder context = new StringBuilder();
        for (Map<String, Object> row : rows) {
            context.append(row.get("chunk_text")).append("\n\n");
        }

        String prompt = promptRenderer.render(model, context.toString(), question);
        log.info("# Constructed Prompt: {}", prompt);

        int tokens = tokenCountEstimator.estimate(prompt);

        if (tokens > maxInputToken) {
            throw new PromptTooLongException("Input exceeds model limit.");
        }


        Instant start = Instant.now();
        String answer = "";
        try {
            answer = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (BadRequestException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("prompt is too long")) {
                throw new PromptTooLongException("Input exceeds model limit.");
            }

            throw ex;
        }
        long responseTimeMs = Duration.between(start, Instant.now()).toMillis();
        log.info("Time taken to process the user query : {} : {} seconds", question, responseTimeMs / 1000.0);

        // Save the question and answer for auditing
        ragAuditService.saveAudit(question, answer, context.toString(), responseTimeMs);

        return new AskResponse(answer, rows.getFirst().get("chunk_text").toString());
    }

    @NotNull
    private List<Map<String, Object>> retrieveRelevantChunks(String embeddingVector) {
        // Pull back a small candidate pool ordered by distance. We filter by
        // similarity threshold in Java (rather than in the WHERE clause) so that
        // rejected near-misses are still visible for logging/tuning.
        List<Map<String,Object>> candidates =
                jdbcTemplate.queryForList("""
                    SELECT chunk_text,
                           (embedding <=> ?::vector) AS distance
                    FROM"""
                        + " " + tableName + " " +
                    """
                    ORDER BY embedding <=> ?::vector
                    LIMIT ?
                """, embeddingVector, embeddingVector, CANDIDATE_POOL_SIZE);

        List<Map<String, Object>> rows = new ArrayList<>();

        for (Map<String, Object> row : candidates) {
            double distance = ((Number) row.get("distance")).doubleValue();
            double similarity = 1 - distance;

            log.debug("Candidate chunk similarity={}, distance={}", String.format("%.4f", similarity), String.format("%.4f", distance));

            if (similarity >= similarityThreshold) {
                rows.add(row);
                if (rows.size() >= topK) {
                    break;
                }
            }
        }
        return rows;
    }

    private void validateQuestion(String question) {
        if(question.length() < 5) {
            throw new IllegalArgumentException("Please provide a more specific question.");
        }
    }

    private String getEmbeddingVector(String question) {
        float[] queryEmbedding = embeddingService.embed(question);
        return Arrays.toString(queryEmbedding);
    }
}
