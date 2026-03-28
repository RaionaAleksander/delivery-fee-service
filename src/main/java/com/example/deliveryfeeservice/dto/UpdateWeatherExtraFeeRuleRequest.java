package com.example.deliveryfeeservice.dto;

import lombok.Data;

@Data
public class UpdateWeatherExtraFeeRuleRequest {
    private String conditionType;

    private String vehicle;

    private Double minValue;
    private Double maxValue;

    private String phenomenon;

    private String actionType;
    private Double extraFee;
}