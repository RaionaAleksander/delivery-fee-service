package com.example.deliveryfeeservice.weather.fetcher;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.deliveryfeeservice.model.City;
import com.example.deliveryfeeservice.model.Weather;
import com.example.deliveryfeeservice.repository.WeatherRepository;
import com.example.deliveryfeeservice.weather.client.WeatherApiClient;
import com.example.deliveryfeeservice.weather.dto.ObservationRoot;
import com.example.deliveryfeeservice.weather.dto.Station;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WeatherDataFetcher {

    private final WeatherApiClient apiClient;
    private final WeatherRepository repository;

    private final Set<String> allowedStations = Set.of(
            "Tallinn-Harku", "Tartu-Tõravere", "Pärnu");

    private static final Map<String, City> stationMap = Map.of(
            "Tallinn-Harku", City.TALLINN,
            "Tartu-Tõravere", City.TARTU,
            "Pärnu", City.PARNU);

    public void fetchAndStore() {
        ObservationRoot root = apiClient.fetch();

        LocalDateTime time = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(root.getTimestamp()),
                ZoneId.systemDefault());

        for (Station station : root.getStations()) {
            if (!allowedStations.contains(station.getName()))
                continue;

            repository.save(Weather.builder()
                    .city(stationMap.get(station.getName()))
                    .temperature(station.getTemperature())
                    .windSpeed(station.getWindSpeed())
                    .phenomenon(station.getPhenomenon())
                    .timestamp(time)
                    .build());
        }
    }
}