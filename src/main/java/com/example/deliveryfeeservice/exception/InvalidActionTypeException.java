package com.example.deliveryfeeservice.exception;

public class InvalidActionTypeException extends RuntimeException {
    public InvalidActionTypeException(String message) {
        super(message);
    }
}