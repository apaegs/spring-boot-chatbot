package org.example.springbootchatbot.model;

public record ChatResponse(
        String reply,
        String sessionId
) {}
