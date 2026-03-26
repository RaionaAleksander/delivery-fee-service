package com.example.deliveryfeeservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.deliveryfeeservice.dto.WeatherExtraFeeRuleResponse;
import com.example.deliveryfeeservice.service.WeatherExtraFeeRuleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/weather-extra-fees")
@RequiredArgsConstructor
public class WeatherExtraFeeRuleController {

    private final WeatherExtraFeeRuleService service;

    @Operation(summary = "Get weather extra fee rules", description = "Returns all weather-based extra fee rules. " +
            "Can be filtered optionally by vehicle type and/or weather condition type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Weather extra fee rules retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid vehicle or condition type")
    })
    @GetMapping
    public List<WeatherExtraFeeRuleResponse> getRules(

            @Parameter(description = "Vehicle type (Car, Scooter, Bike). Optional. Filters results by vehicle", example = "BIKE") @RequestParam(required = false) String vehicle,

            @Parameter(description = "Weather condition type (TEMPERATURE, WIND, PHENOMENON). Optional. Filters results by condition type", example = "TEMPERATURE") @RequestParam(required = false) String condition) {
        return service.getRules(vehicle, condition);
    }
}