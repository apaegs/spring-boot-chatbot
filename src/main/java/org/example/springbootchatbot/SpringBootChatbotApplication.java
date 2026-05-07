package org.example.springbootchatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.resilience.annotation.EnableResilientMethods;

@SpringBootApplication
@EnableResilientMethods
public class SpringBootChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootChatbotApplication.class, args);
    }
}
