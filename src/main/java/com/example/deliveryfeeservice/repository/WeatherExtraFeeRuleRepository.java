package com.example.deliveryfeeservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.deliveryfeeservice.model.ConditionType;
import com.example.deliveryfeeservice.model.VehicleType;
import com.example.deliveryfeeservice.model.WeatherExtraFeeRule;

@Repository
public interface WeatherExtraFeeRuleRepository extends JpaRepository<WeatherExtraFeeRule, Long> {

    List<WeatherExtraFeeRule> findByConditionType(ConditionType conditionType);

    List<WeatherExtraFeeRule> findByVehicle(VehicleType vehicle);

    List<WeatherExtraFeeRule> findByConditionTypeAndVehicle(ConditionType conditionType, VehicleType vehicle);
}