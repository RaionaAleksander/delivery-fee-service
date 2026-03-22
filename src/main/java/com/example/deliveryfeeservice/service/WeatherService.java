package com.example.deliveryfeeservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.deliveryfeeservice.service.weather.WeatherDataFetcher;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WeatherService {

    private final WeatherDataFetcher fetcher;

    @Value("${weather.api.url}")
    private String apiUrl;

    @PostConstruct
    public void init() {
        fetcher.fetchAndStore(apiUrl);
    }

    @Scheduled(cron = "${weather.fetch.cron}")
    void scheduledFetch() {
        fetcher.fetchAndStore(apiUrl);
    }
}