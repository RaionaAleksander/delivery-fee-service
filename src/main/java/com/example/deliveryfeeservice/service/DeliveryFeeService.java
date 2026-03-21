package com.example.deliveryfeeservice.service;

import org.springframework.stereotype.Service;

import com.example.deliveryfeeservice.model.City;
import com.example.deliveryfeeservice.model.VehicleType;
import com.example.deliveryfeeservice.model.Weather;
import com.example.deliveryfeeservice.repository.WeatherRepository;

import java.util.Optional;

@Service
public class DeliveryFeeService {

    private final WeatherRepository weatherRepository;

    private static final String WIND_FORBIDDEN = "Vehicle usage forbidden due to strong wind";
    private static final String PHENOMENON_FORBIDDEN = "Vehicle usage forbidden due to weather conditions";

    public DeliveryFeeService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public double calculate(City city, VehicleType vehicle) {
        Optional<Weather> latestWeatherOpt = weatherRepository.findTopByCityOrderByTimestampDesc(city);

        Weather latestWeather = latestWeatherOpt
                .orElseThrow(() -> new IllegalStateException("No weather data available for city: " + city));

        double baseFee = getRegionalBaseFee(city, vehicle);

        baseFee += getTemperatureExtraFee(latestWeather, vehicle);
        baseFee += getWindExtraFee(latestWeather, vehicle);
        baseFee += getWeatherPhenomenonExtraFee(latestWeather, vehicle);

        return baseFee;
    }

    private double getRegionalBaseFee(City city, VehicleType vehicle) {
        return switch (city) {
            case TALLINN -> switch (vehicle) {
                case CAR -> 4.0;
                case SCOOTER -> 3.5;
                case BIKE -> 3.0;
            };
            case TARTU -> switch (vehicle) {
                case CAR -> 3.5;
                case SCOOTER -> 3.0;
                case BIKE -> 2.5;
            };
            case PARNU -> switch (vehicle) {
                case CAR -> 3.0;
                case SCOOTER -> 2.5;
                case BIKE -> 2.0;
            };
        };
    }

    private double getTemperatureExtraFee(Weather weather, VehicleType vehicle) {
        if (vehicle == VehicleType.CAR)
            return 0.0;

        double temp = weather.getTemperature();
        if (temp < -10)
            return 1.0;
        if (temp <= 0)
            return 0.5;
        return 0.0;
    }

    private double getWindExtraFee(Weather weather, VehicleType vehicle) {
        if (vehicle != VehicleType.BIKE)
            return 0.0;

        double wind = weather.getWindSpeed();
        if (wind > 20)
            throw new IllegalStateException(WIND_FORBIDDEN);
        if (wind >= 10)
            return 0.5;
        return 0.0;
    }

    private double getWeatherPhenomenonExtraFee(Weather weather, VehicleType vehicle) {
        if (vehicle == VehicleType.CAR)
            return 0.0;

        String phenomenon = weather.getPhenomenon();
        if (phenomenon == null || phenomenon.isBlank())
            return 0.0;

        String p = phenomenon.toLowerCase();
        if (p.contains("glaze") || p.contains("hail") || p.contains("thunder")) {
            throw new IllegalStateException(PHENOMENON_FORBIDDEN);
        }
        if (p.contains("snow") || p.contains("sleet"))
            return 1.0;
        if (p.contains("rain"))
            return 0.5;
        return 0.0;
    }
}