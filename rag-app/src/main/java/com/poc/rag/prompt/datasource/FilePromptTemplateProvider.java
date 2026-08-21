package com.poc.rag.prompt.datasource;


import com.poc.rag.prompt.PromptTemplateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilePromptTemplateProvider implements PromptTemplateProvider {

    private final ResourceLoader resourceLoader;

    @Override
    public String loadTemplate(String model) {

        try {
            log.info("Loading prompt from resource file");
            Resource resource = resourceLoader.getResource("classpath:prompts/" + model + ".prompt");
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load prompt for model: " + model, ex);
        }
    }
}
