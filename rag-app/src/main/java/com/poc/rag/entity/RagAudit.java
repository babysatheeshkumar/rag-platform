package com.poc.rag.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.ToString;


import java.time.OffsetDateTime;

@Entity
@Table(
        name = "rag_audit",
        indexes = {
                @Index(name = "index_rag_audit_question_asked_at", columnList = "question_asked_at"),
                @Index(name = "index_rag_audit_chat_model", columnList = "chat_model"),
                @Index(name = "index_rag_audit_username", columnList = "username")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RagAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(
            name = "user_question",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String userQuestion;

    @NotBlank
    @Column(
            name = "llm_answer",
            columnDefinition = "TEXT"
    )
    private String llmAnswer;

    @Column(
            name = "chat_model",
            nullable = false
    )
    private String chatModel;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Column(name = "username")
    private String username;

    @NotBlank
    @Column(
            name = "source_chunk",
            columnDefinition = "TEXT"
    )
    private String sourceChunk;

    @Column(
            name = "question_asked_at",
            nullable = false,
            updatable = false,
            insertable = false
    )
    private OffsetDateTime questionAskedAt;
}
