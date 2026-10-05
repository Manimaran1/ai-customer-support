package com.example.demo.service;

import com.example.demo.model.ChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OrchestrationService {

    private final RagService ragService;
    private final McpClient mcpClient;
    private final KafkaLoggingService kafkaLoggingService;
    private final ChatClient chatClient;

    @Autowired
    public OrchestrationService(RagService ragService,
                                McpClient mcpClient,
                                KafkaLoggingService kafkaLoggingService,
                                ChatClient.Builder chatClientBuilder) {
        this.ragService = ragService;
        this.mcpClient = mcpClient;
        this.kafkaLoggingService = kafkaLoggingService;
        this.chatClient = chatClientBuilder.build();
    }

    public Flux<String> handleQuestion(ChatRequest request) {
        String question = request.getQuestion();

        // Step 3: RAG search (synchronous, fast, in-memory)
        String knowledgeContext = ragService.retrieveRelevantContext(question);

        // Step 4: MCP tool call — reactive, no block()
        Mono<String> customerDataMono;
        if (question.toLowerCase().contains("payment") && request.getTransactionId() != null) {
            System.out.println("🔧 MCP tool call: getPaymentDetails("
                    + request.getCustomerId() + ", " + request.getTransactionId() + ")");
            customerDataMono = mcpClient.getPaymentDetails(
                    request.getCustomerId(), request.getTransactionId());
        } else {
            customerDataMono = Mono.just("");
        }

        // Step 5–8: build prompt, stream answer, log on completion
        return customerDataMono.flatMapMany(customerData -> {

            String systemPrompt = """
                    You are a helpful customer support assistant.
                    Answer the user's question using the provided context and customer data.

                    Knowledge Base Context:
                    %s

                    Customer Data:
                    %s
                    """.formatted(knowledgeContext, customerData);

            UserMessage userMessage = new UserMessage(question);
            SystemMessage systemMessage = new SystemMessage(systemPrompt);
            Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

            StringBuilder fullAnswer = new StringBuilder();

            return chatClient.prompt(prompt).stream().content()
                    .doOnNext(fullAnswer::append)
                    .doOnComplete(() ->
                            kafkaLoggingService.logInteraction(request, fullAnswer.toString()));
        });
    }
}