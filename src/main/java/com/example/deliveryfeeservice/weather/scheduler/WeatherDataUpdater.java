package com.example.deliveryfeeservice.weather.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.deliveryfeeservice.weather.fetcher.WeatherDataFetcher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class WeatherDataUpdater {

    private final WeatherDataFetcher fetcher;

    @Scheduled(cron = "${weather.fetch.cron}")
    void scheduledFetch() {
        fetcher.fetchAndStore();
    }
}