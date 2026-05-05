package org.example.springbootchatbot.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.example.springbootchatbot.model.AiRequest;
import org.example.springbootchatbot.model.AiResponse;
import org.example.springbootchatbot.model.Message;

import java.util.List;

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

    public String sendMessages(List<Message> messages) {
        var request = new AiRequest(model, messages);

        AiResponse response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
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

        if (choice.message() == null || choice.message().content() == null) {
            throw new RuntimeException("AI service returned a choice with no message content");
        }

        return choice.message().content();
    }
}
