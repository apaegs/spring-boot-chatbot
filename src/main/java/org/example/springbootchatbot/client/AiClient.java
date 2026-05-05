package org.example.springbootchatbot.client;

import org.example.springbootchatbot.model.AiRequest;
import org.example.springbootchatbot.model.AiResponse;
import org.example.springbootchatbot.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AiClient {

    private final RestClient restClient;

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.model}")
    private String model;

    public AiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public String sendMessages(java.util.List<Message> messages) {
        var request = new AiRequest(model, messages);

        var response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(AiResponse.class);

        return response.choices().getFirst().message().content();
    }
}
