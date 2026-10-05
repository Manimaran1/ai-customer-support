package com.example.demo.model;

import lombok.Data;
import reactor.core.publisher.Flux;

@Data
public class ChatResponse {
    private Flux<String> customerId;
    private Flux<String> question;
    private Flux<String> transactionId; // optional
}
