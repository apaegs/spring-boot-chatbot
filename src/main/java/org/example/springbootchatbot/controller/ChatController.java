package org.example.springbootchatbot.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.example.springbootchatbot.model.ChatRequest;
import org.example.springbootchatbot.model.ChatResponse;
import org.example.springbootchatbot.service.ChatService;

@RestController
@RequestMapping("/api/v1")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        String reply = chatService.chat(request.personality(), request.message(), request.sessionId());
        return new ChatResponse(reply, request.sessionId());
    }
}
