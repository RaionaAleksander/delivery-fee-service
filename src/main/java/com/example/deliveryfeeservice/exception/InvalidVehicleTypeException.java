package com.example.deliveryfeeservice.exception;

public class InvalidVehicleTypeException extends RuntimeException {
    public InvalidVehicleTypeException(String message) {
        super(message);
    }
}