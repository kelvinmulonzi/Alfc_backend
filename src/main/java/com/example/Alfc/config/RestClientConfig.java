package com.example.Alfc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient youTubeRestClient() {
        return RestClient.builder()
                .baseUrl("https://www.googleapis.com/youtube/v3")
                .build();
    }
}
