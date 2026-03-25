package com.example.deliveryfeeservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.deliveryfeeservice.model.BaseFeeRule;
import com.example.deliveryfeeservice.model.City;
import com.example.deliveryfeeservice.model.VehicleType;

@Repository
public interface BaseFeeRuleRepository extends JpaRepository<BaseFeeRule, Long> {

    Optional<BaseFeeRule> findByCityAndVehicle(City city, VehicleType vehicle);

    List<BaseFeeRule> findByCity(City city);

    List<BaseFeeRule> findByVehicle(VehicleType vehicle);
}