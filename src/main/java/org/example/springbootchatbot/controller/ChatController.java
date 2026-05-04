package org.example.springbootchatbot.controller;

import org.example.springbootchatbot.model.ChatRequest;
import org.example.springbootchatbot.model.ChatResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ChatController {

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return new ChatResponse("Answer coming soon!", request.sessionId());
    }
}
