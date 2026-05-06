package org.example.springbootchatbot.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.example.springbootchatbot.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ConversationMemoryStore {

    private static final int MAX_HISTORY = 20;

    // Caffeine-cache som automatiskt tar bort sessioner
    // som inte använts på X minuter eller när cachen är full
    private final Cache<String, Deque<Message>> sessions;

    public ConversationMemoryStore(
            @Value("${conversation.session.max-size}") int maxSize,
            @Value("${conversation.session.expire-minutes}") int expireMinutes
    ) {
        this.sessions = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterAccess(expireMinutes, TimeUnit.MINUTES)
                .build();
    }

    public List<Message> getHistory(String sessionId) {
        Deque<Message> history = sessions.getIfPresent(sessionId);
        if (history == null) return List.of();
        synchronized (history) {
            return List.copyOf(history);
        }
    }

    public void addMessage(String sessionId, Message message) {
        Deque<Message> history = sessions.get(sessionId, k -> new ArrayDeque<>());
        synchronized (history) {
            history.addLast(message);
            while (history.size() > MAX_HISTORY) {
                history.pollFirst();
            }
        }
    }

    public void clearSession(String sessionId) {
        sessions.invalidate(sessionId);
    }
}
