package com.example.deliveryfeeservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBaseFeeRequest {
    private String city;
    private String vehicle;
    private double fee;
}