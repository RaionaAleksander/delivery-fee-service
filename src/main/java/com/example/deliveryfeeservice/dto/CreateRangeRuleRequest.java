package com.example.deliveryfeeservice.dto;

import lombok.Data;

@Data
public class CreateRangeRuleRequest {

    private String conditionType; // TEMPERATURE or WIND
    private String vehicle;

    private Double minValue; // nullable
    private Double maxValue; // nullable

    private String actionType; // FEE or FORBIDDEN
    private Double extraFee; // required if FEE
}