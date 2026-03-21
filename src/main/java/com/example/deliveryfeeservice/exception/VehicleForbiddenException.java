package com.example.deliveryfeeservice.exception;

public class VehicleForbiddenException extends RuntimeException {
    public VehicleForbiddenException(String message) {
        super(message);
    }
}