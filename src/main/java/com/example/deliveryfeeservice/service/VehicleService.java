package com.example.deliveryfeeservice.service;

import org.springframework.stereotype.Service;

import com.example.deliveryfeeservice.exception.InvalidVehicleTypeException;
import com.example.deliveryfeeservice.model.VehicleType;

@Service
public class VehicleService {
    public VehicleType parseVehicle(String input) {
        try {
            return VehicleType.valueOf(input.toUpperCase());
        } catch (Exception e) {
            throw new InvalidVehicleTypeException("Invalid vehicle: " + input);
        }
    }
}
