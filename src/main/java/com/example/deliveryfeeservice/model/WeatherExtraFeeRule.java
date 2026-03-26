package com.example.deliveryfeeservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherExtraFeeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicle;

    // TEMPERATURE, WIND, PHENOMENON
    @Enumerated(EnumType.STRING)
    private ConditionType conditionType;

    private Double minValue; // for temperature/wind
    private Double maxValue;

    private String phenomenon; // for string conditions

    private Double extraFee;

    private Boolean forbidden;
}