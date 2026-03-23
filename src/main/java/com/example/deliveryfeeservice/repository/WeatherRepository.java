package com.example.deliveryfeeservice.repository;

import com.example.deliveryfeeservice.model.Weather;
import com.example.deliveryfeeservice.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {
    Optional<Weather> findTopByCityOrderByTimestampDesc(City city);

    Optional<Weather> findTopByCityAndTimestampLessThanEqualOrderByTimestampDesc(
            City city,
            LocalDateTime timestamp);
}