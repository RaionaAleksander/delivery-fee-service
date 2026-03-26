package com.example.deliveryfeeservice.controller;

import com.example.deliveryfeeservice.model.City;
import com.example.deliveryfeeservice.model.Weather;
import com.example.deliveryfeeservice.repository.WeatherRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeliveryFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WeatherRepository weatherRepository;

    @BeforeEach
    void setup() {
        weatherRepository.deleteAll();

        Weather weather = new Weather();
        weather.setCity(City.TALLINN);
        weather.setTemperature(-5.0);
        weather.setWindSpeed(5.0);
        weather.setPhenomenon("Snow");
        weather.setTimestamp(LocalDateTime.now());

        weatherRepository.save(weather);
    }

    /**
     * Verifies that the delivery fee endpoint returns correct fee
     * for valid city and vehicle parameters.
     */
    @Test
    void testCalculateFeeEndpoint() throws Exception {
        mockMvc.perform(get("/api/delivery-fee")
                .param("city", "Tallinn")
                .param("vehicle", "BIKE"))
                .andExpect(status().isOk())
                .andExpect(content().string("4.5"));
        // 3.0 base + 0.5 temp + 1 snow
    }

    /**
     * Verifies that the API returns 403 Forbidden
     * when vehicle usage is not allowed due to strong wind conditions.
     */
    @Test
    void testForbiddenDueToWind() throws Exception {
        weatherRepository.deleteAll();

        Weather weather = new Weather();
        weather.setCity(City.TALLINN);
        weather.setTemperature(5.0);
        weather.setWindSpeed(25.0); // strong wind
        weather.setPhenomenon("Clear");
        weather.setTimestamp(LocalDateTime.now());

        weatherRepository.save(weather);

        mockMvc.perform(get("/api/delivery-fee")
                .param("city", "Tallinn")
                .param("vehicle", "BIKE"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error")
                        .value("Vehicle usage forbidden due to strong wind"));
    }

    /**
     * Verifies that the API returns 400 Bad Request
     * when an invalid city parameter is provided.
     */
    @Test
    void testInvalidCityBadRequest() throws Exception {
        mockMvc.perform(get("/api/delivery-fee")
                .param("city", "InvalidCity")
                .param("vehicle", "BIKE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid city: InvalidCity"));
    }

    /**
     * Verifies that the API returns 400 Bad Request
     * when an invalid vehicle parameter is provided.
     */
    @Test
    void testInvalidVehicleBadRequest() throws Exception {
        mockMvc.perform(get("/api/delivery-fee")
                .param("city", "Tallinn")
                .param("vehicle", "Plane"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Invalid vehicle type: Plane"));
    }
}