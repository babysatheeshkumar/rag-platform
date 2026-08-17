package com.poc.rag.service;

import com.poc.rag.entity.RagAudit;
import com.poc.rag.repository.RagAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagAuditService {

    @Value("${spring.ai.anthropic.chat.options.model}")
    private String chatModel;

    private final RagAuditRepository ragAuditRepository;

    @Transactional
    public void saveAudit(String question, String answer, String sourceChunk, Long responseTimeMs) {

        RagAudit audit = RagAudit.builder()
                .userQuestion(question)
                .llmAnswer(answer)
                .sourceChunk(sourceChunk)
                .chatModel(chatModel)
                .responseTimeMs(responseTimeMs)
                .build();

        ragAuditRepository.save(audit);
    }
}