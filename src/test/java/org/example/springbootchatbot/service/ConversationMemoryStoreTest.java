package org.example.springbootchatbot.service;

import org.example.springbootchatbot.model.Message;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ConversationMemoryStoreTest {

    private final ConversationMemoryStore store = new ConversationMemoryStore(4, 100, 30);

    @Test
    void addMessage_andGetHistory_returnsMessagesInOrder() {
        store.addMessage("s1", new Message("user", "hello"));
        store.addMessage("s1", new Message("assistant", "hi"));

        var history = store.getHistory("s1");

        assertThat(history).hasSize(2);
        assertThat(history.get(0).content()).isEqualTo("hello");
        assertThat(history.get(1).content()).isEqualTo("hi");
    }

    @Test
    void addMessage_exceedsMaxHistory_dropsOldestMessages() {
        for (int i = 0; i < 6; i++) {
            store.addMessage("s1", new Message("user", "msg-" + i));
        }

        var history = store.getHistory("s1");

        assertThat(history).hasSize(4);
        assertThat(history.getFirst().content()).isEqualTo("msg-2");
        assertThat(history.getLast().content()).isEqualTo("msg-5");
    }

    @Test
    void clearSession_removesAllMessages() {
        store.addMessage("s1", new Message("user", "hello"));
        store.clearSession("s1");

        assertThat(store.getHistory("s1")).isEmpty();
    }

    @Test
    void differentSessions_areIsolated() {
        store.addMessage("s1", new Message("user", "session one"));
        store.addMessage("s2", new Message("user", "session two"));

        assertThat(store.getHistory("s1")).hasSize(1);
        assertThat(store.getHistory("s1").getFirst().content()).isEqualTo("session one");
        assertThat(store.getHistory("s2").getFirst().content()).isEqualTo("session two");
    }

}
