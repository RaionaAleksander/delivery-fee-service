package com.example.deliveryfeeservice.config;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.deliveryfeeservice.model.BaseFeeRule;
import com.example.deliveryfeeservice.model.City;
import com.example.deliveryfeeservice.model.VehicleType;
import com.example.deliveryfeeservice.repository.BaseFeeRuleRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final BaseFeeRuleRepository repository;

    @PostConstruct
    public void init() {
        if (repository.count() > 0) {
            return;
        }

        repository.saveAll(List.of(
                BaseFeeRule.builder().city(City.TALLINN).vehicle(VehicleType.CAR).fee(4.0).build(),
                BaseFeeRule.builder().city(City.TALLINN).vehicle(VehicleType.SCOOTER).fee(3.5).build(),
                BaseFeeRule.builder().city(City.TALLINN).vehicle(VehicleType.BIKE).fee(3.0).build(),

                BaseFeeRule.builder().city(City.TARTU).vehicle(VehicleType.CAR).fee(3.5).build(),
                BaseFeeRule.builder().city(City.TARTU).vehicle(VehicleType.SCOOTER).fee(3.0).build(),
                BaseFeeRule.builder().city(City.TARTU).vehicle(VehicleType.BIKE).fee(2.5).build(),

                BaseFeeRule.builder().city(City.PARNU).vehicle(VehicleType.CAR).fee(3.0).build(),
                BaseFeeRule.builder().city(City.PARNU).vehicle(VehicleType.SCOOTER).fee(2.5).build(),
                BaseFeeRule.builder().city(City.PARNU).vehicle(VehicleType.BIKE).fee(2.0).build()));
    }
}