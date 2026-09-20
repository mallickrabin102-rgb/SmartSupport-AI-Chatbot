package com.smartsupport.controller;

import com.smartsupport.entity.Chat;
import com.smartsupport.repository.ChatRepository;
import com.smartsupport.repository.FaqRepository;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {
    private final ChatRepository chats;
    private final FaqRepository faqs;

    public DashboardController(ChatRepository chats, FaqRepository faqs) {
        this.chats = chats;
        this.faqs = faqs;
    }

    @GetMapping
    public Map<String,Object> dashboard() {
        List<Chat> all = chats.findAll();
        long answered = all.stream().filter(c -> "ANSWERED".equals(c.getStatus())).count();
        long unanswered = all.stream().filter(c -> "UNANSWERED".equals(c.getStatus())).count();

        Map<String,Object> data = new HashMap<>();
        data.put("totalQuestions", all.size());
        data.put("answered", answered);
        data.put("unanswered", unanswered);
        data.put("faqCount", faqs.count());
        data.put("recentChats", chats.findTop20ByOrderByCreatedAtDesc());
        return data;
    }
}
