package com.example.deliveryfeeservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.deliveryfeeservice.dto.BaseFeeRuleResponse;
import com.example.deliveryfeeservice.service.BaseFeeRuleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/base-fee")
@RequiredArgsConstructor
public class BaseFeeRuleController {

    private final BaseFeeRuleService baseFeeRuleService;

    @Operation(summary = "Get base fee rules", description = "Returns all base fee rules. Can be filtered optionally by city and/or vehicle type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Base fee rules retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid city or vehicle")
    })
    @GetMapping
    public List<BaseFeeRuleResponse> getFees(
            @Parameter(description = "City name (Tallinn, Tartu, Parnu). Optional. If provided, filters results by city", example = "TALLINN") @RequestParam(required = false) String city,
            @Parameter(description = "Vehicle type (Car, Scooter, Bike). Optional. If provided, filters results by vehicle type", example = "CAR") @RequestParam(required = false) String vehicle) {
        return baseFeeRuleService.getFees(city, vehicle);
    }

    @Operation(summary = "Update base fee rule", description = "Updates delivery base fee for a specific city and vehicle type. City and vehicle must already exist in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Base fee updated successfully"),
            @ApiResponse(responseCode = "404", description = "Base fee rule not found")
    })
    @PatchMapping
    public BaseFeeRuleResponse updateFee(
            @Parameter(description = "City name (Tallinn, Tartu, Parnu)", example = "TALLINN") @RequestParam String city,

            @Parameter(description = "Vehicle type (Car, Scooter, Bike)", example = "CAR") @RequestParam String vehicle,

            @Parameter(description = "New base fee value", example = "5.0") @RequestParam double fee) {

        return baseFeeRuleService.updateFee(city, vehicle, fee);
    }
}