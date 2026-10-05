package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class McpClient {

    private final WebClient webClient;

    public McpClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080").build(); // mock MCP server
    }

    public Mono<String> getPaymentDetails(String customerId, String transactionId) {
        // In a real setup, this would call an MCP Server that talks to Payment API.
        // For learning, we mock the response.
       // return Mono.just("Payment transaction " + transactionId + " for customer " + customerId +
        //        ": Declined due to insufficient funds.");
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/payment/details")
                        .queryParam("customerId", customerId)
                        .queryParam("transactionId", transactionId)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }

    // You can add more tool calls: getOrderDetails, getCustomerProfile, etc.
}
