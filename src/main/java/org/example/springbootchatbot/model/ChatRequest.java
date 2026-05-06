package org.example.springbootchatbot.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank(message = "Personality must not be blank")
        String personality,

        @NotBlank(message = "Message must not be blank")
        String message,

        @Size(max = 64, message = "Session ID must not exceed 64 characters")
        @Pattern(regexp = "^[a-zA-Z0-9\\-_]*$", message = "Session ID may only contain letters, numbers, dashes and underscores")
        String sessionId
) {}
