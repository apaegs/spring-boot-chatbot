package org.example.springbootchatbot.model;

import java.util.List;

public record AiResponse(
        List<Choice> choices
) {
    public record Choice(Message message) {}
}
