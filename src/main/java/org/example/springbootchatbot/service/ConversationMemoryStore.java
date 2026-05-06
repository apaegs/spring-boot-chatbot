package org.example.springbootchatbot.service;

import org.example.springbootchatbot.model.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ConversationMemoryStore {

    private static final int MAX_HISTORY = 20;

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Message>> sessions = new ConcurrentHashMap<>();

    public List<Message> getHistory(String sessionId) {
        CopyOnWriteArrayList<Message> history = sessions.get(sessionId);
        if (history == null) return List.of();
        return List.copyOf(history);
    }

    public void addMessage(String sessionId, Message message) {
        CopyOnWriteArrayList<Message> history = sessions.computeIfAbsent(
                sessionId,
                k -> new CopyOnWriteArrayList<>()
        );
        history.add(message);
        while (history.size() > MAX_HISTORY) {
            history.removeFirst();
        }
    }

    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
