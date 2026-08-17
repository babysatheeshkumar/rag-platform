package com.poc.rag.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DecryptEnvironmentPostProcessor implements EnvironmentPostProcessor {


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, @NotNull SpringApplication application) {
        String encrypted = environment.getProperty("spring.ai.anthropic.api-key");
        if (encrypted != null && !encrypted.isBlank() && encrypted.startsWith("ENC(") && encrypted.endsWith(")")) {
            String key = encrypted.substring(4, encrypted.length() - 1);
            String decrypted = ApiKeyCipher.decrypt(key);
            Map<String, Object> overrides = new HashMap<>();
            overrides.put("spring.ai.anthropic.api-key", decrypted);

            environment.getPropertySources().addFirst(new MapPropertySource("decrypted-properties", overrides));
        }
    }
}
