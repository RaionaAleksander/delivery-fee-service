package com.example.deliveryfeeservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VehicleType {
    CAR("Car"),
    SCOOTER("Scooter"),
    BIKE("Bike");

    private final String displayName;
}