package com.example.deliveryfeeservice.model;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "base_fee_rules", uniqueConstraints = @UniqueConstraint(columnNames = { "city", "vehicle" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseFeeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private City city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicle;

    @Column(nullable = false)
    private double fee;
}