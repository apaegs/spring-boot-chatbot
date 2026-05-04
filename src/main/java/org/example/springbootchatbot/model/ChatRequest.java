package org.example.springbootchatbot.model;

public record ChatRequest(
        String personality,
        String message,
        String sessionId
) {}
