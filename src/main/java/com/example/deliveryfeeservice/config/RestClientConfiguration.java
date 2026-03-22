package com.example.deliveryfeeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
class RestClientConfiguration {
    @Bean
    RestClient restClient() {
        return RestClient.create();
    }
}