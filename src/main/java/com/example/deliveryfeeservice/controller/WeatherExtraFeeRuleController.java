package com.example.deliveryfeeservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.deliveryfeeservice.dto.CreatePhenomenonRuleRequest;
import com.example.deliveryfeeservice.dto.CreateRangeRuleRequest;
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

    @Operation(summary = "Create temperature or wind extra fee rule", description = "Creates a new weather-based extra fee rule for TEMPERATURE or WIND conditions. "
            +
            "Rule is defined by a value range (minValue, maxValue), vehicle type, and action type. " +
            "Action type determines whether an extra fee is applied or the vehicle usage is forbidden.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Range-based rule created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/range")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRangeRule(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Range-based rule request. " +
                    "conditionType must be TEMPERATURE or WIND. " +
                    "If actionType = FEE, extraFee must be provided. " +
                    "If actionType = FORBIDDEN, extraFee must be null.", required = true) @RequestBody CreateRangeRuleRequest request) {

        service.createRangeRule(request);
    }

    @Operation(summary = "Create phenomenon extra fee rule", description = "Creates a new weather-based extra fee rule for PHENOMENON conditions. "
            +
            "Rule is defined by phenomenon name (e.g., snow, rain), vehicle type, and action type. " +
            "Action type determines whether an extra fee is applied or the vehicle usage is forbidden.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Phenomenon-based rule created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/phenomenon")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPhenomenonRule(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Phenomenon-based rule request. " +
                    "If actionType = FEE, extraFee must be provided. " +
                    "If actionType = FORBIDDEN, extraFee must be null.", required = true) @RequestBody CreatePhenomenonRuleRequest request) {

        service.createPhenomenonRule(request);
    }

    @Operation(summary = "Delete weather extra fee rule", description = "Deletes a weather-based extra fee rule by its unique ID. "
            +
            "Works for all condition types: TEMPERATURE, WIND, and PHENOMENON.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rule deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Rule not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRule(
            @Parameter(description = "ID of the weather extra fee rule to delete", example = "1") @PathVariable Long id) {

        service.deleteRule(id);
    }
}