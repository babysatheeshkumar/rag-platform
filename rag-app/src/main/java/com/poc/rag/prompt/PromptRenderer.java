package com.poc.rag.prompt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromptRenderer {

    private final PromptService promptService;

    public String render(String model, String context, String question) {

        return promptService.loadTemplate(model)
                .replace("{context}", context)
                .replace("{question}", question);
    }
}
