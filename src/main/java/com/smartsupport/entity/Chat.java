package com.smartsupport.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chats")
public class Chat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=1000)
    private String userMessage;

    @Column(nullable=false, length=2000)
    private String botResponse;

    private String status;
    private LocalDateTime createdAt;

    @PrePersist
    public void beforeSave() { createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String v) { userMessage = v; }
    public String getBotResponse() { return botResponse; }
    public void setBotResponse(String v) { botResponse = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { status = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
