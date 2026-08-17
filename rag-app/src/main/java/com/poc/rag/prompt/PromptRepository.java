package com.poc.rag.prompt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PromptRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<String> findPrompt(String model) {
        List<String> result = jdbcTemplate.queryForList("SELECT prompt_content FROM prompt_templates WHERE model_name = ?", String.class, model);
        return result.stream().findFirst();
    }
}
