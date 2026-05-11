package org.example.springbootchatbot.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PersonalityMapperTest {

    private final PersonalityMapper mapper = new PersonalityMapper();

    @Test
    void getSystemPrompt_null_doesNotThrow() {
        String prompt = mapper.getSystemPrompt(null);
        assertThat(prompt).isNotBlank();
    }

    @Test
    void getSystemPrompt_unknownPersonality_returnsDefault() {
        String prompt = mapper.getSystemPrompt("something-random");
        assertThat(prompt).containsIgnoringCase("helpful assistant");
    }
}
