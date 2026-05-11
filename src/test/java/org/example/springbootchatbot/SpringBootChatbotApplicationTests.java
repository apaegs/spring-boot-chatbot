package org.example.springbootchatbot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.example.springbootchatbot.client.AiClient;

@SpringBootTest
@ActiveProfiles("test")
class SpringBootChatbotApplicationTests {

    @MockitoBean
    AiClient aiClient;

    @Test
    void contextLoads() {
    }
}
