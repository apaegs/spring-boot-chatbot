package org.example.springbootchatbot.client;

import org.example.springbootchatbot.exception.RetryableAiException;
import org.example.springbootchatbot.model.AiRequest;
import org.example.springbootchatbot.model.AiResponse;
import org.example.springbootchatbot.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

/**
 * Client responsible for communicating with the external AI API (OpenRouter).
 *
 * <p>Sends a list of messages to the configured LLM model and returns the
 * generated response. Automatically retries on transient failures (429, 503)
 * using exponential backoff as configured in application properties.
 */
@Component
public class AiClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public AiClient(
            RestClient restClient,
            @Value("${ai.api-key}") String apiKey,
            @Value("${ai.model}") String model
    ) {
        this.restClient = restClient;
        this.apiKey = apiKey;
        this.model = model;
    }

    @Retryable(
            includes = RetryableAiException.class,
            maxRetriesString = "${ai.retry.max-retries}",
            delayString = "${ai.retry.delay}",
            multiplierString = "${ai.retry.multiplier}"
    )
    public String sendMessages(List<Message> messages) {
        var request = new AiRequest(model, messages);

        String idempotencyKey = UUID.nameUUIDFromBytes(
                messages.toString().getBytes()
        ).toString();

        AiResponse response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(status -> status.value() == 429 || status.value() == 503, (req, res) -> {
                    throw new RetryableAiException("AI service temporarily unavailable: " + res.getStatusCode());
                })
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new RuntimeException("AI service rejected the request: " + res.getStatusCode());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new RuntimeException("AI service is unavailable: " + res.getStatusCode());
                })
                .body(AiResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new RuntimeException("AI service returned an empty response");
        }

        var choice = response.choices().getFirst();

        if (choice == null || choice.message() == null || choice.message().content() == null) {
            throw new RuntimeException("AI service returned a choice with no message content");
        }

        return choice.message().content();
    }
}
