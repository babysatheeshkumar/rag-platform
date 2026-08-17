CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS document_chunks_claude_llm
(
    id BIGSERIAL PRIMARY KEY,

    file_name VARCHAR(255),

    chunk_text TEXT,

    embedding VECTOR(768)
);

CREATE TABLE IF NOT EXISTS rag_audit (
    id BIGSERIAL PRIMARY KEY,
    user_question TEXT NOT NULL,
    llm_answer TEXT,
    source_chunk TEXT,
    chat_model VARCHAR(255) NOT NULL,
    response_time_ms BIGINT,
    username VARCHAR(255),
    question_asked_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS index_rag_audit_question_asked_at
ON rag_audit(question_asked_at);

CREATE INDEX IF NOT EXISTS index_rag_audit_chat_model
ON rag_audit(chat_model);

CREATE INDEX IF NOT EXISTS index_rag_audit_username
ON rag_audit(username);

CREATE TABLE IF NOT EXISTS prompt_templates
(
    model_name     VARCHAR(100) PRIMARY KEY,
    prompt_content TEXT NOT NULL,
    version        INTEGER,
    updated_at     TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO public.prompt_templates
    (model_name, prompt_content, version)
VALUES
    ('claude-opus-4-5-20251101',
    $$
     You are a document assistant.

     Use STRICTLY the supplied context.

     Context:
     {context}

     Question:
     {question}

     If answer is not found,
     say:
     'Answer not found in document'.
  $$, 2)
ON CONFLICT (model_name) DO NOTHING;