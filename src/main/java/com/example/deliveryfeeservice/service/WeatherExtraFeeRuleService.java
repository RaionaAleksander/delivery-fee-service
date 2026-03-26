package com.example.deliveryfeeservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.deliveryfeeservice.dto.WeatherExtraFeeRuleResponse;
import com.example.deliveryfeeservice.model.ConditionType;
import com.example.deliveryfeeservice.model.VehicleType;
import com.example.deliveryfeeservice.model.WeatherExtraFeeRule;
import com.example.deliveryfeeservice.repository.WeatherExtraFeeRuleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherExtraFeeRuleService {

    private final WeatherExtraFeeRuleRepository repository;
    private final VehicleService vehicleService;
    private final ConditionTypeService conditionTypeService;

    public List<WeatherExtraFeeRuleResponse> getRules(String vehicleStr, String conditionStr) {

        VehicleType vehicle = vehicleStr != null ? vehicleService.parseVehicle(vehicleStr) : null;
        ConditionType condition = conditionStr != null ? conditionTypeService.parseCondition(conditionStr) : null;

        List<WeatherExtraFeeRule> rules;

        if (vehicle != null && condition != null) {
            rules = repository.findByConditionTypeAndVehicle(condition, vehicle);
        } else if (vehicle != null) {
            rules = repository.findByVehicle(vehicle);
        } else if (condition != null) {
            rules = repository.findByConditionType(condition);
        } else {
            rules = repository.findAll();
        }

        return rules.stream()
                .map(this::toResponse)
                .toList();
    }

    private WeatherExtraFeeRuleResponse toResponse(WeatherExtraFeeRule rule) {
        return WeatherExtraFeeRuleResponse.builder()
                .conditionType(rule.getConditionType().name())
                .vehicle(rule.getVehicle().name())
                .minValue(rule.getMinValue())
                .maxValue(rule.getMaxValue())
                .phenomenon(rule.getPhenomenon())
                .extraFee(rule.getExtraFee())
                .forbidden(rule.getForbidden())
                .build();
    }
}