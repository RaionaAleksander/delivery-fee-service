package com.example.deliveryfeeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseFeeRuleResponse {
    private String city;
    private String vehicle;
    private double fee;
}