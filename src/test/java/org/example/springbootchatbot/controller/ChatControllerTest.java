package org.example.springbootchatbot.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnableWireMock
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectWireMock
    private WireMockServer wireMock;

    @BeforeEach
    void setUp() {
        wireMock.resetAll();
    }

    @Test
    void chat_returnsReply_whenLlmRespondsSuccessfully() throws Exception {
        wireMock.stubFor(WireMock.post(urlEqualTo("/chat/completions"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "choices": [
                                    {
                                        "message": {
                                            "role": "assistant",
                                            "content": "A for-loop is like a vinyl record — it keeps spinning until you tell it to stop."
                                        }
                                    }
                                ]
                            }
                        """)));

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "personality": "music-nerd",
                                "message": "What is a for-loop?",
                                "sessionId": "test-123"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("A for-loop is like a vinyl record — it keeps spinning until you tell it to stop."))
                .andExpect(jsonPath("$.sessionId").value("test-123"));
    }

    @Test
    void chat_returns400_whenPersonalityIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "personality": "",
                                "message": "Hello!",
                                "sessionId": "test-123"
                            }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void chat_returns400_whenMessageIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "personality": "music-nerd",
                                "message": "",
                                "sessionId": "test-123"
                            }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void chat_retriesAndReturns503_whenLlmReturns429() throws Exception {
        wireMock.stubFor(WireMock.post(urlEqualTo("/chat/completions"))
                .willReturn(aResponse()
                        .withStatus(429)));

        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "personality": "music-nerd",
                                "message": "Hello!",
                                "sessionId": "test-123"
                            }
                        """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("AI service temporarily unavailable"));

        wireMock.verify(4, postRequestedFor(urlEqualTo("/chat/completions")));
    }

    @Test
    void chat_returns400_whenSessionIdHasInvalidCharacters() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personality": "default",
                            "message": "Hello!",
                            "sessionId": "invalid session!"
                        }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void chat_returns400_whenSessionIdIsTooLong() throws Exception {
        String longId = "a".repeat(65);
        mockMvc.perform(post("/api/v1/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "personality": "default",
                            "message": "Hello!",
                            "sessionId": "%s"
                        }
                    """.formatted(longId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }
}
