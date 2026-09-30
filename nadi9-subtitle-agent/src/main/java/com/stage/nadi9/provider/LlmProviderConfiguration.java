package com.stage.nadi9.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlmProviderConfiguration {

    @Bean
    public LlmProvider activeLlmProvider(
            @Value("${nadi9.provider:mock}") String provider,
            @Value("${nadi9.llm.url}") String url,
            @Value("${nadi9.llm.model}") String model,
            @Value("${nadi9.llm.api-key}") String apiKey) {

        if ("http".equalsIgnoreCase(provider)) {
            return new HttpLlmProvider(
                    url,
                    model,
                    apiKey
            );
        }

        return new MockLlmProvider();
    }
}