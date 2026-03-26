package com.example.deliveryfeeservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.deliveryfeeservice.exception.BaseFeeRuleNotFoundException;
import com.example.deliveryfeeservice.exception.VehicleForbiddenException;
import com.example.deliveryfeeservice.exception.WeatherDataNotFoundException;
import com.example.deliveryfeeservice.model.BaseFeeRule;
import com.example.deliveryfeeservice.model.City;
import com.example.deliveryfeeservice.model.ConditionType;
import com.example.deliveryfeeservice.model.VehicleType;
import com.example.deliveryfeeservice.model.Weather;
import com.example.deliveryfeeservice.model.WeatherExtraFeeRule;
import com.example.deliveryfeeservice.repository.BaseFeeRuleRepository;
import com.example.deliveryfeeservice.repository.WeatherExtraFeeRuleRepository;
import com.example.deliveryfeeservice.repository.WeatherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryFeeService {

    private final WeatherRepository weatherRepository;
    private final BaseFeeRuleRepository baseFeeRuleRepository;
    private final WeatherExtraFeeRuleRepository weatherExtraFeeRuleRepository;
    private final CityService cityService;
    private final VehicleService vehicleService;

    private static final String WIND_FORBIDDEN = "Vehicle usage forbidden due to strong wind";
    private static final String PHENOMENON_FORBIDDEN = "Vehicle usage forbidden due to weather conditions";

    public double calculate(String cityStr, String vehicleStr, LocalDateTime datetime) {

        City city = cityService.parseCity(cityStr);
        VehicleType vehicle = vehicleService.parseVehicle(vehicleStr);

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

        List<WeatherExtraFeeRule> rules = weatherExtraFeeRuleRepository.findByConditionTypeAndVehicle(
                ConditionType.TEMPERATURE, vehicle);

        double extraFee = 0.0;

        for (WeatherExtraFeeRule rule : rules) {

            boolean matchesMin = rule.getMinValue() == null || temp >= rule.getMinValue();
            boolean matchesMax = rule.getMaxValue() == null || temp <= rule.getMaxValue();

            if (matchesMin && matchesMax) {
                extraFee += rule.getExtraFee() != null ? rule.getExtraFee() : 0.0;
            }
        }

        return extraFee;
    }

    private double getWindExtraFee(Weather weather, VehicleType vehicle) {
        if (vehicle != VehicleType.BIKE)
            return 0.0;

        double wind = weather.getWindSpeed();

        List<WeatherExtraFeeRule> rules = weatherExtraFeeRuleRepository.findByConditionTypeAndVehicle(
                ConditionType.WIND, vehicle);

        double extraFee = 0.0;

        for (WeatherExtraFeeRule rule : rules) {

            boolean matchesMin = rule.getMinValue() == null || wind >= rule.getMinValue();
            boolean matchesMax = rule.getMaxValue() == null || wind <= rule.getMaxValue();

            if (matchesMin && matchesMax) {
                if (Boolean.TRUE.equals(rule.getForbidden())) {
                    throw new VehicleForbiddenException(WIND_FORBIDDEN);
                }
                if (rule.getExtraFee() != null) {
                    extraFee += rule.getExtraFee();
                }
            }
        }

        return extraFee;
    }

    private double getWeatherPhenomenonExtraFee(Weather weather, VehicleType vehicle) {
        if (vehicle == VehicleType.CAR)
            return 0.0;

        String phenomenon = weather.getPhenomenon();
        if (phenomenon == null || phenomenon.isBlank())
            return 0.0;

        String p = phenomenon.toLowerCase();

        List<WeatherExtraFeeRule> rules = weatherExtraFeeRuleRepository.findByConditionTypeAndVehicle(
                ConditionType.PHENOMENON, vehicle);

        double extraFee = 0.0;

        for (WeatherExtraFeeRule rule : rules) {

            if (rule.getPhenomenon() == null || rule.getPhenomenon().isBlank()) {
                continue;
            }

            String rulePhenomenon = rule.getPhenomenon().toLowerCase();

            if (p.contains(rulePhenomenon)) {

                if (Boolean.TRUE.equals(rule.getForbidden())) {
                    throw new VehicleForbiddenException(PHENOMENON_FORBIDDEN);
                }

                if (rule.getExtraFee() != null) {
                    extraFee += rule.getExtraFee();
                }
            }
        }

        return extraFee;
    }
}