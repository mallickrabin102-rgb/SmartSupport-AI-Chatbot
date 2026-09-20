package com.smartsupport.dto;

public class ChatResponse {
    private String response;
    private String status;

    public ChatResponse(String response, String status) {
        this.response = response;
        this.status = status;
    }

    public String getResponse() { return response; }
    public String getStatus() { return status; }
}
