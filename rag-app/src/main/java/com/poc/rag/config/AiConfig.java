package com.poc.rag.config;

import com.poc.rag.util.RecursiveTextChunker;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tokenizer.JTokkitTokenCountEstimator;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AiConfig {

    @Value("${app.chunk-size}")
    private int chunkSize;

    @Bean
    ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

    @Bean
    TokenCountEstimator tokenCountEstimator() {
        return new JTokkitTokenCountEstimator();
    }

    @Bean
    RecursiveTextChunker recursiveTextChunker() {
        return new RecursiveTextChunker(chunkSize);
    }



}