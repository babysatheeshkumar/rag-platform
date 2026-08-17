package com.poc.rag.util;
import java.util.ArrayList;
import java.util.List;

public class RecursiveTextChunker {

    private final int maxChunkSize;

    public RecursiveTextChunker(int maxChunkSize) {
        this.maxChunkSize = maxChunkSize;
    }

    public List<String> chunk(String text) {
        return chunkRecursively(text);
    }

    private List<String> chunkRecursively(String text) {

        if (text.length() <= maxChunkSize) {
            return List.of(text.trim());
        }

        // Level 1: Paragraph split
        if (text.contains("\n\n")) {
            return splitAndRecurse(text, "\\n+");
        }

        // Level 2: Sentence split
        if (text.matches(".*[.!?].*")) {
            return splitAndRecurse(text, "[.!?]+");
        }

        // Level 3: Word split
        if (text.contains(" ")) {
            return splitAndRecurse(text, "\\s+");
        }

        // Level 4: Hard split
        return hardSplit(text);
    }

    private List<String> splitAndRecurse(String text, String delimiter) {

        String[] parts = text.split(delimiter);

        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String part : parts) {
            String candidate = current.isEmpty() ? part : current + " " + part;

            if (candidate.length() <= maxChunkSize) {
                current.setLength(0);
                current.append(candidate);
            } else {

                if (!current.isEmpty()) {
                    chunks.addAll(chunkRecursively(current.toString()));
                }

                current.setLength(0);
                current.append(part);
            }
        }

        if (!current.isEmpty()) {
            chunks.addAll(chunkRecursively(current.toString()));
        }

        return chunks;
    }

    private List<String> hardSplit(String text) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += maxChunkSize) {
            chunks.add(text.substring(i, Math.min(i + maxChunkSize, text.length())));
        }
        return chunks;
    }

    public static void main(String[] args) {
        String document = """
                what is your name?
                My name is baby.
                Nice to meet you Baby! Welcome to our team.
                
                I hope this is a great team to work with. And you will learn a lot from them. Each one of them are specialized in different technologies.
                
                As the Machine Learning is growing now, you can expect many opportunities here.
                
            """;

        RecursiveTextChunker chunker = new RecursiveTextChunker(100);
        List<String> chunks = chunker.chunk(document);
    }
}
