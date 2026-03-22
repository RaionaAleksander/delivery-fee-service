package com.example.deliveryfeeservice.service.weather;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.example.deliveryfeeservice.exception.WeatherDataFetchException;
import com.example.deliveryfeeservice.model.City;
import com.example.deliveryfeeservice.model.Weather;
import com.example.deliveryfeeservice.repository.WeatherRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class WeatherDataFetcher {
    private final WeatherRepository weatherRepository;
    private final Set<String> allowedStations = Set.of("Tallinn-Harku", "Tartu-Tõravere", "Pärnu");

    public void fetchAndStore(String apiUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)));
            document.getDocumentElement().normalize();

            LocalDateTime observationTime = extractObservationTimestamp(document);

            NodeList stations = document.getElementsByTagName("station");

            for (int i = 0; i < stations.getLength(); i++) {
                Element station = (Element) stations.item(i);
                String name = station.getElementsByTagName("name").item(0).getTextContent();

                if (!allowedStations.contains(name))
                    continue;

                City city = mapStationToCity(name);
                String phenomenon = getTagValue(station, "phenomenon");
                String temperature = getTagValue(station, "airtemperature");
                String windSpeed = getTagValue(station, "windspeed");

                weatherRepository.save(Weather.builder()
                        .city(city)
                        .temperature(Double.parseDouble(temperature))
                        .windSpeed(Double.parseDouble(windSpeed))
                        .phenomenon(phenomenon)
                        .timestamp(observationTime)
                        .build());
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new WeatherDataFetchException("Failed to fetch or parse weather data", e);
        }
    }

    private LocalDateTime extractObservationTimestamp(Document document) {
        Element root = document.getDocumentElement();
        String timestampStr = root.getAttribute("timestamp");
        long unixTimestamp = Long.parseLong(timestampStr);
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimestamp), ZoneId.systemDefault());
    }

    private City mapStationToCity(String stationName) {
        return switch (stationName) {
            case "Tallinn-Harku" -> City.TALLINN;
            case "Tartu-Tõravere" -> City.TARTU;
            case "Pärnu" -> City.PARNU;
            default -> null;
        };
    }

    private String getTagValue(Element element, String tagName) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list.getLength() == 0 || list.item(0) == null)
            return null;
        return list.item(0).getTextContent();
    }

}