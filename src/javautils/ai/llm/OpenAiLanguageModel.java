package javautils.ai.llm;

import javautils.math.Range;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OpenAiLanguageModel implements LanguageModel {
    public static final Range RANGE_TEMPERATURE = new Range(0D, 2D);
    public static final Range RANGE_TOKENS = new Range(0D, null);

    private final HttpClient httpClient;
    private final String apiKey;
    private final String baseUrl;
    private String model = "gpt-4o-mini";
    private double temperature = 1D;
    private int maxTokens = 1024;

    public OpenAiLanguageModel(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public String complete(String prompt) {
        List<Message> messages = List.of(
            new Message(Message.Role.USER, prompt)
        );
        return complete(messages);
    }

    @Override
    public CompletableFuture<String> completeAsync(String prompt) {
        return CompletableFuture.supplyAsync(() -> complete(prompt));
    }

    @Override
    public void setTemperature(double temperature) {
        this.temperature = RANGE_TEMPERATURE.snapToRange(temperature);
    }

    @Override
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = Math.max(0, maxTokens);
    }

    @Override
    public void setModel(String modelName) {
        this.model = modelName;
    }

    @Override
    public String getModel() {
        return this.model;
    }

    @Override
    public String complete(final List<Message> messages) {
        try {
            // Build request payload
            String jsonPayload = buildRequestPayload(messages);

            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

            // Send request
            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

            // Parse and return response
            return parseResponse(response.body());

        } catch (Exception e) {
            throw new RuntimeException("Failed to complete request: " + e.getMessage(), e);
        }
    }

    private String parseResponse(String body) {
        throw new UnsupportedOperationException("TODO: Implement!");
    }

    private String buildRequestPayload(List<Message> messages) {
        throw new UnsupportedOperationException("TODO: Implement!");
    }


}