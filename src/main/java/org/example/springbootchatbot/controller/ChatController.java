package org.example.springbootchatbot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.springbootchatbot.model.ChatRequest;
import org.example.springbootchatbot.model.ChatResponse;
import org.example.springbootchatbot.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Chat", description = "Endpoints for chatting with AI personalities")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(
            summary = "Send a message to the AI",
            description = "Send a message with a chosen personality and optional session ID for conversation memory"
    )
    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        String reply = chatService.chat(request.personality(), request.message(), request.sessionId());
        return new ChatResponse(reply, request.sessionId());
    }
}
