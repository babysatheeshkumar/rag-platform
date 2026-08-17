package com.poc.rag.prompt;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromptService {
    private final PromptProviderFactory factory;

    public String loadTemplate(String model) {
        return factory.getProvider().loadTemplate(model);
    }
}
