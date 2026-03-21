package com.example.deliveryfeeservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.deliveryfeeservice.model.City;
import com.example.deliveryfeeservice.model.VehicleType;
import com.example.deliveryfeeservice.model.Weather;
import com.example.deliveryfeeservice.repository.WeatherRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

class DeliveryFeeServiceTest {

    private WeatherRepository weatherRepository;
    private DeliveryFeeService deliveryFeeService;

    @BeforeEach
    void setup() {
        weatherRepository = mock(WeatherRepository.class);
        deliveryFeeService = new DeliveryFeeService(weatherRepository);
    }

    // 1.
    /**
     * Verifies that the base fee is correctly applied for a bike in Tartu
     * when no additional weather conditions affect the price.
     */
    @Test
    void testBaseFeeBikeTartu() {
        Weather weather = new Weather();
        weather.setCity(City.TARTU);
        weather.setTemperature(5.0);
        weather.setWindSpeed(2.0);
        weather.setPhenomenon("Clear");
        weather.setTimestamp(LocalDateTime.now());

        when(weatherRepository.findTopByCityOrderByTimestampDesc(City.TARTU))
                .thenReturn(Optional.of(weather));

        double fee = deliveryFeeService.calculate(City.TARTU, VehicleType.BIKE);
        assertEquals(2.5, fee);
    }

    // 2.
    /**
     * Verifies that an extra fee is added for low temperatures (between -10°C and
     * 0°C)
     * when using a scooter.
     */
    @Test
    void testTemperatureExtraFeeScooter() {
        Weather weather = new Weather();
        weather.setCity(City.TALLINN);
        weather.setTemperature(-5.0); // between -10 and 0
        weather.setWindSpeed(2.0);
        weather.setPhenomenon("Clear");
        weather.setTimestamp(LocalDateTime.now());

        when(weatherRepository.findTopByCityOrderByTimestampDesc(City.TALLINN))
                .thenReturn(Optional.of(weather));

        double fee = deliveryFeeService.calculate(City.TALLINN, VehicleType.SCOOTER);
        assertEquals(4.0, fee); // 3.5 base + 0.5 temp
    }

    // 3.
    /**
     * Verifies that bike usage is forbidden when wind speed exceeds the allowed
     * limit.
     */
    @Test
    void testWindForbiddenBike() {
        Weather weather = new Weather();
        weather.setCity(City.PARNU);
        weather.setTemperature(0.0);
        weather.setWindSpeed(25.0); // strong wind
        weather.setPhenomenon("Clear");
        weather.setTimestamp(LocalDateTime.now());

        when(weatherRepository.findTopByCityOrderByTimestampDesc(City.PARNU))
                .thenReturn(Optional.of(weather));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> deliveryFeeService.calculate(City.PARNU, VehicleType.BIKE));
        assertEquals("Vehicle usage forbidden due to strong wind", exception.getMessage());
    }

    // 4.
    /**
     * Verifies that an additional fee is applied when snow or sleet is present.
     */
    @Test
    void testWeatherPhenomenonExtraFeeBike() {
        Weather weather = new Weather();
        weather.setCity(City.TARTU);
        weather.setTemperature(-2.0);
        weather.setWindSpeed(3.0);
        weather.setPhenomenon("Light snow");
        weather.setTimestamp(LocalDateTime.now());

        when(weatherRepository.findTopByCityOrderByTimestampDesc(City.TARTU))
                .thenReturn(Optional.of(weather));

        double fee = deliveryFeeService.calculate(City.TARTU, VehicleType.BIKE);
        assertEquals(4.0, fee); // 2.5 base + 0.5 temp + 1 snow
    }

    // 5.
    /**
     * Verifies that cars are not affected by weather conditions and only the base
     * fee is applied.
     */
    @Test
    void testCarNoExtraFees() {
        Weather weather = new Weather();
        weather.setCity(City.TALLINN);
        weather.setTemperature(-20.0);
        weather.setWindSpeed(30.0);
        weather.setPhenomenon("Hail");
        weather.setTimestamp(LocalDateTime.now());

        when(weatherRepository.findTopByCityOrderByTimestampDesc(City.TALLINN))
                .thenReturn(Optional.of(weather));

        double fee = deliveryFeeService.calculate(City.TALLINN, VehicleType.CAR);

        assertEquals(4.0, fee);
    }

    // 6.
    /**
     * Verifies that rain adds an extra fee for scooter usage.
     */
    @Test
    void testRainExtraFeeScooter() {
        Weather weather = new Weather();
        weather.setCity(City.PARNU);
        weather.setTemperature(5.0);
        weather.setWindSpeed(3.0);
        weather.setPhenomenon("Light rain");
        weather.setTimestamp(LocalDateTime.now());

        when(weatherRepository.findTopByCityOrderByTimestampDesc(City.PARNU))
                .thenReturn(Optional.of(weather));

        double fee = deliveryFeeService.calculate(City.PARNU, VehicleType.SCOOTER);

        assertEquals(3.0, fee); // 2.5 base + 0.5 rain
    }

    // 7.
    /**
     * Verifies that an exception is thrown when no weather data is available.
     */
    @Test
    void testNoWeatherData() {
        when(weatherRepository.findTopByCityOrderByTimestampDesc(City.TARTU))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> deliveryFeeService.calculate(City.TARTU, VehicleType.BIKE));
    }

    // 8.
    /**
     * Verifies that vehicle usage is forbidden when hazardous weather phenomena
     * (e.g., hail) occur.
     */
    @Test
    void testForbiddenPhenomenon() {
        Weather weather = new Weather();
        weather.setCity(City.TALLINN);
        weather.setTemperature(0.0);
        weather.setWindSpeed(5.0);
        weather.setPhenomenon("Hail");
        weather.setTimestamp(LocalDateTime.now());

        when(weatherRepository.findTopByCityOrderByTimestampDesc(City.TALLINN))
                .thenReturn(Optional.of(weather));

        assertThrows(IllegalStateException.class,
                () -> deliveryFeeService.calculate(City.TALLINN, VehicleType.SCOOTER));
    }

    // 9.
    /**
     * Verifies that vehicle usage is forbidden during thunderstorm conditions.
     */
    @Test
    void testThunderForbidden() {
        Weather weather = new Weather();
        weather.setCity(City.TARTU);
        weather.setTemperature(2.0);
        weather.setWindSpeed(3.0);
        weather.setPhenomenon("Thunderstorm");
        weather.setTimestamp(LocalDateTime.now());

        when(weatherRepository.findTopByCityOrderByTimestampDesc(City.TARTU))
                .thenReturn(Optional.of(weather));

        assertThrows(IllegalStateException.class, () -> deliveryFeeService.calculate(City.TARTU, VehicleType.BIKE));
    }

    // 10.
    /**
     * Verifies that bike usage is forbidden when wind speed exceeds the extreme
     * threshold.
     */
    @Test
    void testExtremeWindBike() {
        Weather weather = new Weather();
        weather.setCity(City.PARNU);
        weather.setTemperature(5.0);
        weather.setWindSpeed(21.0);
        weather.setPhenomenon("Clear");
        weather.setTimestamp(LocalDateTime.now());

        when(weatherRepository.findTopByCityOrderByTimestampDesc(City.PARNU))
                .thenReturn(Optional.of(weather));

        assertThrows(IllegalStateException.class, () -> deliveryFeeService.calculate(City.PARNU, VehicleType.BIKE));
    }
}