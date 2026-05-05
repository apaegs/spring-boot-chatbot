package org.example.springbootchatbot.service;

import org.springframework.stereotype.Service;
import org.example.springbootchatbot.client.AiClient;
import org.example.springbootchatbot.config.PersonalityMapper;
import org.example.springbootchatbot.model.Message;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final AiClient aiClient;
    private final PersonalityMapper personalityMapper;

    public ChatService(AiClient aiClient, PersonalityMapper personalityMapper) {
        this.aiClient = aiClient;
        this.personalityMapper = personalityMapper;
    }

    public String chat(String personality, String userMessage) {
        String systemPrompt = personalityMapper.getSystemPrompt(personality);

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", systemPrompt));
        messages.add(new Message("user", userMessage));

        return aiClient.sendMessages(messages);
    }
}
