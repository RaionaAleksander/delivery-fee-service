package com.example.deliveryfeeservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.deliveryfeeservice.dto.BaseFeeRuleResponse;
import com.example.deliveryfeeservice.exception.BaseFeeRuleNotFoundException;
import com.example.deliveryfeeservice.model.BaseFeeRule;
import com.example.deliveryfeeservice.model.City;
import com.example.deliveryfeeservice.model.VehicleType;
import com.example.deliveryfeeservice.repository.BaseFeeRuleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BaseFeeRuleService {

    private final BaseFeeRuleRepository repository;
    private final VehicleService vehicleService;
    private final CityService cityService;

    public List<BaseFeeRuleResponse> getFees(String cityStr, String vehicleStr) {

        City city = cityStr != null ? cityService.parseCity(cityStr) : null;
        VehicleType vehicle = vehicleStr != null ? vehicleService.parseVehicle(vehicleStr) : null;

        List<BaseFeeRule> rules;

        if (city != null && vehicle != null) {
            rules = repository.findByCityAndVehicle(city, vehicle)
                    .map(List::of)
                    .orElse(List.of());
        } else if (city != null) {
            rules = repository.findByCity(city);
        } else if (vehicle != null) {
            rules = repository.findByVehicle(vehicle);
        } else {
            rules = repository.findAll();
        }

        return rules.stream()
                .map(r -> new BaseFeeRuleResponse(
                        r.getCity().name(),
                        r.getVehicle().name(),
                        r.getFee()))
                .toList();
    }

    public BaseFeeRuleResponse updateFee(String cityStr, String vehicleStr, double newFee) {

        City city = cityService.parseCity(cityStr);
        VehicleType vehicle = vehicleService.parseVehicle(vehicleStr);

        BaseFeeRule rule = repository.findByCityAndVehicle(city, vehicle)
                .orElseThrow(() -> new BaseFeeRuleNotFoundException(
                        "Base fee rule not found for city: " + city + ", vehicle: " + vehicle));

        rule.setFee(newFee);
        repository.save(rule);

        return new BaseFeeRuleResponse(
                rule.getCity().name(),
                rule.getVehicle().name(),
                rule.getFee());
    }
}