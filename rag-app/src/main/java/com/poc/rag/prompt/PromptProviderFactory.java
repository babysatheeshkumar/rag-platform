package com.poc.rag.prompt;


import com.poc.rag.prompt.datasource.DbPromptTemplateProvider;
import com.poc.rag.prompt.datasource.FilePromptTemplateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PromptProviderFactory {

    private final List<PromptTemplateProvider> providers;

    @Value("${app.llm.prompt.source}")
    private String source;

    public PromptTemplateProvider getProvider() {
        return switch (source) {
            case "db" ->
                    find(DbPromptTemplateProvider.class);
            default ->
                    find(FilePromptTemplateProvider.class);
        };
    }

    private PromptTemplateProvider find(Class<?> type) {
        return providers.stream().filter(type::isInstance)
                .findFirst()
                .orElseThrow();
    }
}
