package com.example.deliveryfeeservice.weather.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.example.deliveryfeeservice.exception.WeatherDataFetchException;
import com.example.deliveryfeeservice.weather.dto.ObservationRoot;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WeatherApiClient {

    private final RestClient restClient;

    @Value("${weather.api.url}")
    private String apiUrl;

    public ObservationRoot fetch() {
        try {
            return restClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .body(ObservationRoot.class);
        } catch (RestClientException e) {
            throw new WeatherDataFetchException("Failed to fetch weather data", e);
        }
    }
}