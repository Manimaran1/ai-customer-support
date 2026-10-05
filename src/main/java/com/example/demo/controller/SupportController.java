package com.example.demo.controller;

import com.example.demo.model.ChatRequest;
import com.example.demo.service.OrchestrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class SupportController {

    @Autowired
    private OrchestrationService orchestrationService;

    @PostMapping(value = "/api/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody ChatRequest request) {
        return orchestrationService.handleQuestion(request);
    }
}