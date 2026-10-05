package com.example.demo.model;

import lombok.Data;

@Data
public class ChatRequest {
    private String customerId;
    private String question;
    private String transactionId; // optional
}