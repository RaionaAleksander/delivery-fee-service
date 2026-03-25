package com.example.deliveryfeeservice.controller;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.example.deliveryfeeservice.service.DeliveryFeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/delivery-fee")
class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;

    @Operation(summary = "Calculate delivery fee", description = "Calculates delivery fee based on city, vehicle type and weather conditions. "
            +
            "Optionally, a datetime can be provided to calculate fee based on historical weather data.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery fee calculated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid city or vehicle"),
            @ApiResponse(responseCode = "403", description = "Vehicle usage forbidden due to weather conditions"),
            @ApiResponse(responseCode = "404", description = "Weather data not found")
    })
    @GetMapping
    double calculateFee(
            @Parameter(description = "City name (Tallinn, Tartu, Parnu)", example = "TALLINN") @RequestParam String city,

            @Parameter(description = "Vehicle type (Car, Scooter, Bike)", example = "BIKE") @RequestParam String vehicle,

            @Parameter(description = "Optional datetime in ISO format (yyyy-MM-ddTHH:mm). If not provided, latest weather data is used", example = "2026-03-26T12:00") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime) {

        return deliveryFeeService.calculate(city, vehicle, datetime);
    }
}