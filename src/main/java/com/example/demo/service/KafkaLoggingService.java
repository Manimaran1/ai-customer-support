package com.example.demo.service;

import com.example.demo.model.ChatRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaLoggingService {

    @Autowired(required = false)
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void logInteraction(ChatRequest request, String answer) {
        if (kafkaTemplate == null) {
            System.out.println("⚠️ Kafka not configured — skipping log");
            return;
        }
        try {
            String event = String.format(
                    "Customer: %s | Question: %s | Answer length: %d",
                    request.getCustomerId(),
                    request.getQuestion(),
                    answer == null ? 0 : answer.length());
            kafkaTemplate.send("customer-interactions", event);
            System.out.println("📤 Published interaction to Kafka");
        } catch (Exception e) {
            System.err.println("⚠️ Kafka publish failed: " + e.getMessage());
        }
    }
}