package com.poc.rag.prompt.datasource;

import com.poc.rag.prompt.PromptRepository;
import com.poc.rag.prompt.PromptTemplateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DbPromptTemplateProvider implements PromptTemplateProvider {

    private final PromptRepository repository;

    @Override
    public String loadTemplate(String model) {
        return repository.findPrompt(model).orElseThrow(() -> new IllegalArgumentException("Prompt not found: " + model));
    }
}
