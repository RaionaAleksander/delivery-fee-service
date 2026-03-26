package com.example.deliveryfeeservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherExtraFeeRuleResponse {

    private String conditionType;
    private String vehicle;

    private Double minValue;
    private Double maxValue;

    private String phenomenon;

    private Double extraFee;
    private Boolean forbidden;
}