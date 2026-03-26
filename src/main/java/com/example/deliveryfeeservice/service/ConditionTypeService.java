package com.example.deliveryfeeservice.service;

import org.springframework.stereotype.Service;

import com.example.deliveryfeeservice.exception.InvalidConditionTypeException;
import com.example.deliveryfeeservice.model.ConditionType;

@Service
public class ConditionTypeService {

    public ConditionType parseCondition(String input) {
        try {
            return ConditionType.valueOf(input.toUpperCase());
        } catch (Exception ex) {
            throw new InvalidConditionTypeException("Invalid condition type: " + input);
        }
    }
}