package com.example.deliveryfeeservice.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "weather_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** City of observation */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private City city;

    /** Air temperature in Celsius */
    @Column(nullable = false)
    private Double temperature;

    /** Wind speed in m/s */
    @Column(nullable = false)
    private Double windSpeed;

    /** Weather phenomenon description (e.g., Light snow) */
    @Column(nullable = false)
    private String phenomenon;

    /** Timestamp of observation */
    @Column(nullable = false)
    private LocalDateTime timestamp;
}