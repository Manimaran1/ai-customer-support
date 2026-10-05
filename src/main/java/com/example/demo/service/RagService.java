package com.example.demo.service;

import com.example.demo.util.DocumentLoader;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final List<DocumentChunk> documentStore = new ArrayList<>();
    private final EmbeddingModel embeddingModel;

    @Autowired
    public RagService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
        try {
            loadDocuments();
            System.out.println("✅ RAG documents loaded: " + documentStore.size() + " chunks");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to load RAG documents: " + e.getMessage());
        }
    }

    private void loadDocuments() {
        List<String> docs = DocumentLoader.loadSampleDocuments();
        for (String doc : docs) {
            String[] chunks = doc.split("\n\n");
            for (String chunk : chunks) {
                if (!chunk.isBlank()) {
                    float[] embedding = embeddingModel.embed(chunk);
                    documentStore.add(new DocumentChunk(chunk, embedding));
                }
            }
        }
    }

    public String retrieveRelevantContext(String query) {
        if (documentStore.isEmpty()) {
            return "";
        }
        float[] queryEmbedding = embeddingModel.embed(query);
        List<DocumentChunk> top = documentStore.stream()
                .sorted(Comparator.comparingDouble(
                        d -> -cosineSimilarity(queryEmbedding, d.embedding)))
                .limit(3)
                .collect(Collectors.toList());
        return top.stream().map(d -> d.content).collect(Collectors.joining("\n\n"));
    }

    private double cosineSimilarity(float[] v1, float[] v2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        int len = Math.min(v1.length, v2.length);
        for (int i = 0; i < len; i++) {
            dot += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }
        if (norm1 == 0 || norm2 == 0) return 0;
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    private static class DocumentChunk {
        String content;
        float[] embedding;
        DocumentChunk(String content, float[] embedding) {
            this.content = content;
            this.embedding = embedding;
        }
    }
}