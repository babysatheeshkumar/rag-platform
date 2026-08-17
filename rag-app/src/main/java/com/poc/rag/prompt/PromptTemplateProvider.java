package com.poc.rag.prompt;

public interface PromptTemplateProvider {
    String loadTemplate(String model);
}
