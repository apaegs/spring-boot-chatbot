package org.example.springbootchatbot.service;

import org.example.springbootchatbot.model.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationMemoryStore {

    private final ConcurrentHashMap<String, List<Message>> sessions = new ConcurrentHashMap<>();

    public List<Message> getHistory(String sessionId) {
        return sessions.getOrDefault(sessionId, new ArrayList<>());
    }

    public void addMessage(String sessionId, Message message) {
        sessions.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(message);
    }

    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
