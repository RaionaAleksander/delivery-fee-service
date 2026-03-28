package com.example.deliveryfeeservice.exception;

public class WeatherExtraFeeRuleNotFoundException extends RuntimeException {
    public WeatherExtraFeeRuleNotFoundException(Long id) {
        super("Weather extra fee rule not found: " + id);
    }
}