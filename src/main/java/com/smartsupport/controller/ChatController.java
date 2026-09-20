package com.smartsupport.controller;

import com.smartsupport.dto.ChatRequest;
import com.smartsupport.dto.ChatResponse;
import com.smartsupport.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatController {
    private final ChatService service;

    public ChatController(ChatService service) { this.service = service; }

    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return service.processMessage(request.getMessage());
    }
}
