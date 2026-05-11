package org.example.springbootchatbot.service;

import org.example.springbootchatbot.client.AiClient;
import org.example.springbootchatbot.config.PersonalityMapper;
import org.example.springbootchatbot.model.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private AiClient aiClient;

    @Mock
    private PersonalityMapper personalityMapper;

    @Mock
    private ConversationMemoryStore conversationMemoryStore;

    @InjectMocks
    private ChatService chatService;

    @Test
    void chat_returnsReplyFromAiClient() {
        when(personalityMapper.getSystemPrompt("default")).thenReturn("You are helpful.");
        when(conversationMemoryStore.getHistory("s1")).thenReturn(List.of());
        when(aiClient.sendMessages(anyList(), anyString())).thenReturn("reply");

        String result = chatService.chat("default", "hello", "s1");

        assertThat(result).isEqualTo("reply");
    }

    @Test
    void chat_withoutSessionId_doesNotTouchMemoryStore() {
        when(personalityMapper.getSystemPrompt("default")).thenReturn("You are helpful.");
        when(aiClient.sendMessages(anyList(), anyString())).thenReturn("reply");

        chatService.chat("default", "hello", null);

        verifyNoInteractions(conversationMemoryStore);
    }

    @Test
    void chat_withSessionId_savesUserMessageAndReply() {
        when(personalityMapper.getSystemPrompt("default")).thenReturn("You are helpful.");
        when(conversationMemoryStore.getHistory("s1")).thenReturn(List.of());
        when(aiClient.sendMessages(anyList(), anyString())).thenReturn("reply");

        chatService.chat("default", "hello", "s1");

        verify(conversationMemoryStore).addMessage("s1", new Message("user", "hello"));
        verify(conversationMemoryStore).addMessage("s1", new Message("assistant", "reply"));
    }
}
