package com.example.deliveryfeeservice.service;

import org.springframework.stereotype.Service;

import com.example.deliveryfeeservice.exception.BaseFeeRuleNotFoundException;
import com.example.deliveryfeeservice.exception.VehicleForbiddenException;
import com.example.deliveryfeeservice.exception.WeatherDataNotFoundException;
import com.example.deliveryfeeservice.model.BaseFeeRule;
import com.example.deliveryfeeservice.model.City;
import com.example.deliveryfeeservice.model.VehicleType;
import com.example.deliveryfeeservice.model.Weather;
import com.example.deliveryfeeservice.repository.BaseFeeRuleRepository;
import com.example.deliveryfeeservice.repository.WeatherRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryFeeService {

    private final WeatherRepository weatherRepository;
    private final BaseFeeRuleRepository baseFeeRuleRepository;

    private static final String WIND_FORBIDDEN = "Vehicle usage forbidden due to strong wind";
    private static final String PHENOMENON_FORBIDDEN = "Vehicle usage forbidden due to weather conditions";

    public double calculate(City city, VehicleType vehicle, LocalDateTime datetime) {
        Optional<Weather> weatherOpt;

        if (datetime != null) {
            weatherOpt = weatherRepository
                    .findTopByCityAndTimestampLessThanEqualOrderByTimestampDesc(city, datetime);
        } else {
            weatherOpt = weatherRepository
                    .findTopByCityOrderByTimestampDesc(city);
        }

        Weather weather = weatherOpt.orElseThrow(() -> {
            if (datetime != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return new WeatherDataNotFoundException(
                        "No weather data available before " + datetime.format(formatter) + " for city: " + city);
            } else {
                return new WeatherDataNotFoundException(
                        "No weather data available for city: " + city);
            }
        });

        double baseFee = getRegionalBaseFee(city, vehicle);

        baseFee += getTemperatureExtraFee(weather, vehicle);
        baseFee += getWindExtraFee(weather, vehicle);
        baseFee += getWeatherPhenomenonExtraFee(weather, vehicle);

        return baseFee;
    }

    private double getRegionalBaseFee(City city, VehicleType vehicle) {
        return baseFeeRuleRepository.findByCityAndVehicle(city, vehicle)
                .map(BaseFeeRule::getFee)
                .orElseThrow(() -> new BaseFeeRuleNotFoundException(
                        "Base fee rule not found for city: " + city + ", vehicle: " + vehicle));
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
            throw new VehicleForbiddenException(WIND_FORBIDDEN);
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
            throw new VehicleForbiddenException(PHENOMENON_FORBIDDEN);
        }
        if (p.contains("snow") || p.contains("sleet"))
            return 1.0;
        if (p.contains("rain"))
            return 0.5;
        return 0.0;
    }
}