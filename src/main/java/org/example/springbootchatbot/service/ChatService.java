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
    private final ConversationMemoryStore conversationMemoryStore;

    public ChatService(AiClient aiClient, PersonalityMapper personalityMapper, ConversationMemoryStore conversationMemoryStore) {
        this.aiClient = aiClient;
        this.personalityMapper = personalityMapper;
        this.conversationMemoryStore = conversationMemoryStore;
    }

    public String chat(String personality, String userMessage, String sessionId) {
        String systemPrompt = personalityMapper.getSystemPrompt(personality);

        List<Message> messages = new ArrayList<>();

        messages.add(new Message("system", systemPrompt));

        if (sessionId != null && !sessionId.isBlank()) {
            messages.addAll(conversationMemoryStore.getHistory(sessionId));
        }

        messages.add(new Message("user", userMessage));

        String reply = aiClient.sendMessages(messages);

        if (sessionId != null && !sessionId.isBlank()) {
            conversationMemoryStore.addMessage(sessionId, new Message("user", userMessage));
            conversationMemoryStore.addMessage(sessionId, new Message("assistant", reply));

        }

        return reply;
    }
}
