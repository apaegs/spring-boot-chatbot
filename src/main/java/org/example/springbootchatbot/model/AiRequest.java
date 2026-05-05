package org.example.springbootchatbot.model;

import java.util.List;

public record AiRequest(
        String model,
        List<Message> messages
) {}
