package com.poc.rag.security;

import java.util.HashMap;
import java.util.Map;

public class ApiKeyCipher {

    private static final String ORIGINAL  = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
    private static final String ENCRYPTED = "qazwsxedcrfv_tgbyhnujmikolp0512938-476PLOKMIJNUHBYGVTFCRDXESZWAQ";
    private static final Map<Character, Character> ENCRYPTION_MAP = new HashMap<>();
    private static final Map<Character, Character> DECRYPTION_MAP = new HashMap<>();

    static {

        for (int i = 0; i < ORIGINAL.length(); i++) {
            ENCRYPTION_MAP.put(ORIGINAL.charAt(i), ENCRYPTED.charAt(i));
            DECRYPTION_MAP.put(ENCRYPTED.charAt(i), ORIGINAL.charAt(i));
        }
    }

    public static String encrypt(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            String key = ENCRYPTION_MAP.getOrDefault(c, c).toString();
            result.append(key);
        }

        return result.toString();
    }

    public static String decrypt(String encrypted) {
        StringBuilder result = new StringBuilder();
        for (char c : encrypted.toCharArray()) {
            result.append(DECRYPTION_MAP.getOrDefault(c, c));
        }

        return result.toString();
    }

}
