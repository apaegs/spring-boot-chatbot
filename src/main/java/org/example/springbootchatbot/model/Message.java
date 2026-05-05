package org.example.springbootchatbot.model;

public record Message(
        String role,    // "system", "user" eller "assistant"
        String content
) {}
