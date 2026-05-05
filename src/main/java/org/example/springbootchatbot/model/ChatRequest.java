package org.example.springbootchatbot.model;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank(message = "Personality must not be blank")
        String personality,

        @NotBlank(message = "Message must not be blank")
        String message,

        String sessionId
) {}
