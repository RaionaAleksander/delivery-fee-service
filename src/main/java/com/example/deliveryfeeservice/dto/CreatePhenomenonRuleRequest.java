package com.example.deliveryfeeservice.dto;

import com.example.deliveryfeeservice.model.RuleActionType;

import lombok.Data;

@Data
public class CreatePhenomenonRuleRequest {

    private String vehicle;
    private String phenomenon;

    private String actionType; // FEE or FORBIDDEN
    private Double extraFee; // required if FEE
}