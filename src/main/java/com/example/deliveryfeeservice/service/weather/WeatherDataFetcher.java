package com.example.deliveryfeeservice.service.weather;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.deliveryfeeservice.exception.WeatherDataFetchException;
import com.example.deliveryfeeservice.model.City;
import com.example.deliveryfeeservice.model.Weather;
import com.example.deliveryfeeservice.repository.WeatherRepository;
import com.example.deliveryfeeservice.service.weather.dto.ObservationRoot;
import com.example.deliveryfeeservice.service.weather.dto.Station;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class WeatherDataFetcher {
    private final WeatherRepository weatherRepository;
    private final Set<String> allowedStations = Set.of("Tallinn-Harku", "Tartu-Tõravere", "Pärnu");

    private final XmlMapper xmlMapper = new XmlMapper();

    public void fetchAndStore(String apiUrl) {
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);

            ObservationRoot root = xmlMapper.readValue(response, ObservationRoot.class);

            LocalDateTime observationTime = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(root.getTimestamp()),
                    ZoneId.systemDefault());

            for (Station station : root.getStations()) {

                if (!allowedStations.contains(station.getName())) {
                    continue;
                }

                City city = mapStationToCity(station.getName());

                weatherRepository.save(Weather.builder()
                        .city(city)
                        .temperature(station.getTemperature())
                        .windSpeed(station.getWindSpeed())
                        .phenomenon(station.getPhenomenon())
                        .timestamp(observationTime)
                        .build());
            }
        } catch (Exception e) {
            throw new WeatherDataFetchException("Failed to fetch or parse weather data", e);
        }
    }

    private City mapStationToCity(String stationName) {
        return switch (stationName) {
            case "Tallinn-Harku" -> City.TALLINN;
            case "Tartu-Tõravere" -> City.TARTU;
            case "Pärnu" -> City.PARNU;
            default -> null;
        };
    }
}