package com.example.deliveryfeeservice.service;

import org.springframework.stereotype.Service;

import com.example.deliveryfeeservice.exception.InvalidActionTypeException;
import com.example.deliveryfeeservice.model.RuleActionType;

@Service
public class ActionTypeService {
    public RuleActionType parseActionType(String input) {
        if (input == null || input.isBlank()) {
            throw new InvalidActionTypeException("Action type is required");
        }

        try {
            return RuleActionType.valueOf(input.toUpperCase());
        } catch (Exception e) {
            throw new InvalidActionTypeException("Invalid action type: " + input);
        }
    }
}
