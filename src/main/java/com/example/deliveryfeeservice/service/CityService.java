package com.example.deliveryfeeservice.service;

import java.text.Normalizer;

import org.springframework.stereotype.Service;

import com.example.deliveryfeeservice.exception.InvalidCityException;
import com.example.deliveryfeeservice.model.City;

@Service
public class CityService {
    public City parseCity(String input) {
        if (input == null || input.isBlank()) {
            throw new InvalidCityException("Invalid city");
        }

        try {
            return City.valueOf(normalize(input));
        } catch (Exception e) {
            throw new InvalidCityException("Invalid city");
        }
    }

    private String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return normalized.toUpperCase();
    }
}