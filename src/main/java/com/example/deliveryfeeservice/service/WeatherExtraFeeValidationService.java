package com.example.deliveryfeeservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.deliveryfeeservice.dto.CreatePhenomenonRuleRequest;
import com.example.deliveryfeeservice.dto.CreateRangeRuleRequest;
import com.example.deliveryfeeservice.exception.InvalidWeatherExtraFeeRuleException;
import com.example.deliveryfeeservice.model.ConditionType;
import com.example.deliveryfeeservice.model.RuleActionType;
import com.example.deliveryfeeservice.model.WeatherExtraFeeRule;

@Service
public class WeatherExtraFeeValidationService {

    public void validateRangeRule(CreateRangeRuleRequest request) {
        validateNotNull(request.getConditionType(), "Condition type is required");
        validateNotNull(request.getVehicle(), "Vehicle is required");
        validateNotNull(request.getActionType(), "Action type is required");

        validateMinMax(request.getMinValue(), request.getMaxValue());
    }

    public void validatePhenomenonRule(CreatePhenomenonRuleRequest request) {
        validateNotNull(request.getVehicle(), "Vehicle is required");
        validateNotNull(request.getPhenomenon(), "Phenomenon is required");
        validateNotNull(request.getActionType(), "Action type is required");

        validateNotBlank(request.getPhenomenon(), "Phenomenon cannot be blank");
    }

    // ------------------- CONDITION -------------------

    public void validateConditionNotPhenomenon(ConditionType type) {
        if (type == ConditionType.PHENOMENON) {
            throw new InvalidWeatherExtraFeeRuleException(
                    "PHENOMENON is not allowed in range endpoint. Use /phenomenon API");
        }
    }

    // ------------------- ACTION -------------------

    public void validateAction(RuleActionType type, Double fee) {
        if (type == RuleActionType.FEE) {
            if (fee == null) {
                throw new InvalidWeatherExtraFeeRuleException("Extra fee must be provided for FEE action");
            }
            if (fee <= 0) {
                throw new InvalidWeatherExtraFeeRuleException("Extra fee must be greater than 0");
            }
        }

        if (type == RuleActionType.FORBIDDEN) {
            if (fee != null) {
                throw new InvalidWeatherExtraFeeRuleException("Extra fee must be null when action is FORBIDDEN");
            }
        }
    }

    // ------------------- BASIC -------------------

    private void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new InvalidWeatherExtraFeeRuleException(message);
        }
    }

    private void validateNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidWeatherExtraFeeRuleException(message);
        }
    }

    // ------------------- RANGE -------------------

    public void validateMinMax(Double min, Double max) {
        if (min == null && max == null) {
            throw new InvalidWeatherExtraFeeRuleException("At least one of minValue or maxValue must be provided");
        }

        if (min != null && max != null) {
            if (min.equals(max)) {
                throw new InvalidWeatherExtraFeeRuleException(
                        "minValue and maxValue cannot be equal");
            }

            if (max < min) {
                throw new InvalidWeatherExtraFeeRuleException(
                        "maxValue must be greater than minValue");
            }
        }
    }

    // ------------------- OVERLAP -------------------

    public void validateNoOverlap(
            List<WeatherExtraFeeRule> existingRules,
            Double newMin,
            Double newMax) {
        for (WeatherExtraFeeRule rule : existingRules) {

            Double min = rule.getMinValue();
            Double max = rule.getMaxValue();

            if (overlaps(min, max, newMin, newMax)) {
                throw new InvalidWeatherExtraFeeRuleException(
                        "Range overlaps with existing rule: [" + min + ", " + max + "]");
            }
        }
    }

    private boolean overlaps(Double min1, Double max1, Double min2, Double max2) {
        double aMin = min1 != null ? min1 : Double.NEGATIVE_INFINITY;
        double aMax = max1 != null ? max1 : Double.POSITIVE_INFINITY;

        double bMin = min2 != null ? min2 : Double.NEGATIVE_INFINITY;
        double bMax = max2 != null ? max2 : Double.POSITIVE_INFINITY;

        return aMin < bMax && bMin < aMax;
    }

    // ------------------- PHENOMENON / TEMPERATURE or WIND -------------------

    public void validatePhenomenonFields(String phenomenon, Double min, Double max) {

        if (phenomenon == null || phenomenon.isBlank()) {
            throw new InvalidWeatherExtraFeeRuleException(
                    "Phenomenon must be provided for PHENOMENON condition");
        }

        if (min != null || max != null) {
            throw new InvalidWeatherExtraFeeRuleException(
                    "minValue and maxValue must be null for PHENOMENON condition");
        }
    }

    public void validateRangeFields(String phenomenon, Double min, Double max) {

        if (phenomenon != null && !phenomenon.isBlank()) {
            throw new InvalidWeatherExtraFeeRuleException(
                    "Phenomenon must be null for TEMPERATURE and WIND conditions");
        }

        validateMinMax(min, max);
    }
}