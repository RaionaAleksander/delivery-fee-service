package com.example.deliveryfeeservice.controller;

import org.springframework.web.bind.annotation.*;

import com.example.deliveryfeeservice.model.City;
import com.example.deliveryfeeservice.model.VehicleType;
import com.example.deliveryfeeservice.service.DeliveryFeeService;
import com.example.deliveryfeeservice.service.CityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/delivery-fee")
public class DeliveryFeeController {

    private final DeliveryFeeService deliveryFeeService;
    private final CityService cityService;

    @Operation(summary = "Calculate delivery fee based on city, vehicle and current weather")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery fee calculated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid city or vehicle"),
            @ApiResponse(responseCode = "403", description = "Vehicle usage forbidden due to weather conditions")
    })
    @GetMapping
    public double calculateFee(
            @Parameter(description = "City name (Tallinn, Tartu, Parnu)") @RequestParam String city,

            @Parameter(description = "Vehicle type (Car, Scooter, Bike)") @RequestParam String vehicle) {

        City cityEnum = cityService.parseCity(city);

        VehicleType vehicleEnum;
        try {
            vehicleEnum = VehicleType.valueOf(vehicle.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid vehicle type");
        }

        return deliveryFeeService.calculate(cityEnum, vehicleEnum);
    }
}