package com.example.deliveryfeeservice.config;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.deliveryfeeservice.model.ConditionType;
import com.example.deliveryfeeservice.model.VehicleType;
import com.example.deliveryfeeservice.model.WeatherExtraFeeRule;
import com.example.deliveryfeeservice.repository.WeatherExtraFeeRuleRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WeatherExtraFeeDataInitializer {
    private final WeatherExtraFeeRuleRepository repository;

    @PostConstruct
    public void init() {

        if (repository.count() > 0) {
            return;
        }

        repository.saveAll(List.of(

                // TEMPERATURE
                WeatherExtraFeeRule.builder()
                        .vehicle(VehicleType.BIKE)
                        .conditionType(ConditionType.TEMPERATURE)
                        .maxValue(-10.0)
                        .extraFee(1.0)
                        .build(),

                WeatherExtraFeeRule.builder()
                        .vehicle(VehicleType.BIKE)
                        .conditionType(ConditionType.TEMPERATURE)
                        .minValue(-10.0)
                        .maxValue(0.0)
                        .extraFee(0.5)
                        .build(),

                WeatherExtraFeeRule.builder()
                        .vehicle(VehicleType.SCOOTER)
                        .conditionType(ConditionType.TEMPERATURE)
                        .maxValue(-10.0)
                        .extraFee(1.0)
                        .build(),

                WeatherExtraFeeRule.builder()
                        .vehicle(VehicleType.SCOOTER)
                        .conditionType(ConditionType.TEMPERATURE)
                        .minValue(-10.0)
                        .maxValue(0.0)
                        .extraFee(0.5)
                        .build(),

                // WIND (only BIKE)
                WeatherExtraFeeRule.builder()
                        .vehicle(VehicleType.BIKE)
                        .conditionType(ConditionType.WIND)
                        .minValue(10.0)
                        .maxValue(20.0)
                        .extraFee(0.5)
                        .build(),

                WeatherExtraFeeRule.builder()
                        .vehicle(VehicleType.BIKE)
                        .conditionType(ConditionType.WIND)
                        .minValue(20.0)
                        .forbidden(true)
                        .build(),

                // PHENOMENON
                WeatherExtraFeeRule.builder()
                        .vehicle(VehicleType.BIKE)
                        .conditionType(ConditionType.PHENOMENON)
                        .phenomenon("snow")
                        .extraFee(1.0)
                        .build(),

                WeatherExtraFeeRule.builder()
                        .vehicle(VehicleType.BIKE)
                        .conditionType(ConditionType.PHENOMENON)
                        .phenomenon("sleet")
                        .extraFee(1.0)
                        .build(),

                WeatherExtraFeeRule.builder()
                        .vehicle(VehicleType.BIKE)
                        .conditionType(ConditionType.PHENOMENON)
                        .phenomenon("rain")
                        .extraFee(0.5)
                        .build(),

                WeatherExtraFeeRule.builder()
                        .vehicle(VehicleType.BIKE)
                        .conditionType(ConditionType.PHENOMENON)
                        .phenomenon("glaze")
                        .forbidden(true)
                        .build(),

                WeatherExtraFeeRule.builder()
                        .vehicle(VehicleType.BIKE)
                        .conditionType(ConditionType.PHENOMENON)
                        .phenomenon("hail")
                        .forbidden(true)
                        .build(),

                WeatherExtraFeeRule.builder()
                        .vehicle(VehicleType.BIKE)
                        .conditionType(ConditionType.PHENOMENON)
                        .phenomenon("thunder")
                        .forbidden(true)
                        .build()));
    }
}
