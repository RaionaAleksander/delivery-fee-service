package com.example.deliveryfeeservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.deliveryfeeservice.dto.CreatePhenomenonRuleRequest;
import com.example.deliveryfeeservice.dto.CreateRangeRuleRequest;
import com.example.deliveryfeeservice.dto.WeatherExtraFeeRuleResponse;
import com.example.deliveryfeeservice.exception.WeatherExtraFeeRuleNotFoundException;
import com.example.deliveryfeeservice.model.ConditionType;
import com.example.deliveryfeeservice.model.RuleActionType;
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
    private final WeatherExtraFeeValidationService validationService;
    private final ActionTypeService actionTypeService;

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

    public void createRangeRule(CreateRangeRuleRequest request) {

        validationService.validateRangeRule(request);

        ConditionType condition = conditionTypeService.parseCondition(request.getConditionType());
        validationService.validateConditionNotPhenomenon(condition);

        VehicleType vehicle = vehicleService.parseVehicle(request.getVehicle());
        RuleActionType actionType = actionTypeService.parseActionType(request.getActionType());
        validationService.validateAction(actionType, request.getExtraFee());

        List<WeatherExtraFeeRule> existingRules = repository.findByConditionTypeAndVehicle(condition, vehicle);

        validationService.validateNoOverlap(
                existingRules,
                request.getMinValue(),
                request.getMaxValue());

        WeatherExtraFeeRule rule = new WeatherExtraFeeRule();
        rule.setConditionType(condition);
        rule.setVehicle(vehicle);
        rule.setMinValue(request.getMinValue());
        rule.setMaxValue(request.getMaxValue());

        if (actionType == RuleActionType.FEE) {
            rule.setExtraFee(request.getExtraFee());
            rule.setForbidden(null);
        } else {
            rule.setExtraFee(null);
            rule.setForbidden(true);
        }

        repository.save(rule);
    }

    public void createPhenomenonRule(CreatePhenomenonRuleRequest request) {

        validationService.validatePhenomenonRule(request);

        VehicleType vehicle = vehicleService.parseVehicle(request.getVehicle());

        RuleActionType actionType = actionTypeService.parseActionType(request.getActionType());
        validationService.validateAction(actionType, request.getExtraFee());

        WeatherExtraFeeRule rule = new WeatherExtraFeeRule();
        rule.setConditionType(ConditionType.PHENOMENON);
        rule.setVehicle(vehicle);
        rule.setPhenomenon(request.getPhenomenon());

        if (actionType == RuleActionType.FEE) {
            rule.setExtraFee(request.getExtraFee());
            rule.setForbidden(null);
        } else {
            rule.setExtraFee(null);
            rule.setForbidden(true);
        }

        repository.save(rule);
    }

    public void deleteRule(Long id) {
        WeatherExtraFeeRule rule = repository.findById(id)
                .orElseThrow(() -> new WeatherExtraFeeRuleNotFoundException(id));

        repository.delete(rule);
    }
}