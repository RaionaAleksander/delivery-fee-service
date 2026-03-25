package com.example.deliveryfeeservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.deliveryfeeservice.exception.VehicleForbiddenException;
import com.example.deliveryfeeservice.exception.WeatherDataNotFoundException;
import com.example.deliveryfeeservice.model.BaseFeeRule;
import com.example.deliveryfeeservice.model.City;
import com.example.deliveryfeeservice.model.VehicleType;
import com.example.deliveryfeeservice.model.Weather;
import com.example.deliveryfeeservice.repository.BaseFeeRuleRepository;
import com.example.deliveryfeeservice.repository.WeatherRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

class DeliveryFeeServiceTest {

    private WeatherRepository weatherRepository;
    private BaseFeeRuleRepository baseFeeRuleRepository;
    private DeliveryFeeService deliveryFeeService;

    @BeforeEach
    void setup() {
        weatherRepository = mock(WeatherRepository.class);
        baseFeeRuleRepository = mock(BaseFeeRuleRepository.class);
        deliveryFeeService = new DeliveryFeeService(weatherRepository, baseFeeRuleRepository);

        when(baseFeeRuleRepository.findByCityAndVehicle(any(), any()))
                .thenAnswer(invocation -> {
                    City city = invocation.getArgument(0);
                    VehicleType vehicle = invocation.getArgument(1);

                    double fee = switch (city) {
                        case TALLINN -> switch (vehicle) {
                            case CAR -> 4.0;
                            case SCOOTER -> 3.5;
                            case BIKE -> 3.0;
                        };
                        case TARTU -> switch (vehicle) {
                            case CAR -> 3.5;
                            case SCOOTER -> 3.0;
                            case BIKE -> 2.5;
                        };
                        case PARNU -> switch (vehicle) {
                            case CAR -> 3.0;
                            case SCOOTER -> 2.5;
                            case BIKE -> 2.0;
                        };
                    };

                    return Optional.of(
                            BaseFeeRule.builder()
                                    .city(city)
                                    .vehicle(vehicle)
                                    .fee(fee)
                                    .build());
                });
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

        double fee = deliveryFeeService.calculate(City.TARTU, VehicleType.BIKE, null);
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

        double fee = deliveryFeeService.calculate(City.TALLINN, VehicleType.SCOOTER, null);
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

        VehicleForbiddenException exception = assertThrows(VehicleForbiddenException.class,
                () -> deliveryFeeService.calculate(City.PARNU, VehicleType.BIKE, null));
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

        double fee = deliveryFeeService.calculate(City.TARTU, VehicleType.BIKE, null);
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

        double fee = deliveryFeeService.calculate(City.TALLINN, VehicleType.CAR, null);

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

        double fee = deliveryFeeService.calculate(City.PARNU, VehicleType.SCOOTER, null);

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

        assertThrows(WeatherDataNotFoundException.class,
                () -> deliveryFeeService.calculate(City.TARTU, VehicleType.BIKE, null));
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

        assertThrows(VehicleForbiddenException.class,
                () -> deliveryFeeService.calculate(City.TALLINN, VehicleType.SCOOTER, null));
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

        assertThrows(VehicleForbiddenException.class,
                () -> deliveryFeeService.calculate(City.TARTU, VehicleType.BIKE, null));
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

        assertThrows(VehicleForbiddenException.class,
                () -> deliveryFeeService.calculate(City.PARNU, VehicleType.BIKE, null));
    }

    // 11.
    /**
     * Verifies that when a datetime is provided, the service retrieves weather
     * data using the timestamp-based query and calculates the fee accordingly.
     */
    @Test
    void testCalculateUsesDatetimeQuery() {
        LocalDateTime time = LocalDateTime.of(2026, 3, 23, 12, 0);

        Weather weather = new Weather();
        weather.setCity(City.TALLINN);
        weather.setTemperature(5.0);
        weather.setWindSpeed(2.0);
        weather.setPhenomenon(null);
        weather.setTimestamp(time.minusMinutes(5));

        when(weatherRepository
                .findTopByCityAndTimestampLessThanEqualOrderByTimestampDesc(City.TALLINN, time))
                .thenReturn(Optional.of(weather));

        double fee = deliveryFeeService.calculate(City.TALLINN, VehicleType.CAR, time);

        assertEquals(4.0, fee); // base only

        verify(weatherRepository)
                .findTopByCityAndTimestampLessThanEqualOrderByTimestampDesc(City.TALLINN, time);
    }

    // 12.
    /**
     * Verifies that an exception is thrown when no weather data is available
     * for the given city before the specified datetime.
     */
    @Test
    void testNoWeatherDataForDatetime() {
        LocalDateTime time = LocalDateTime.of(2026, 3, 23, 12, 0);

        when(weatherRepository
                .findTopByCityAndTimestampLessThanEqualOrderByTimestampDesc(City.TARTU, time))
                .thenReturn(Optional.empty());

        assertThrows(WeatherDataNotFoundException.class,
                () -> deliveryFeeService.calculate(City.TARTU, VehicleType.BIKE, time));
    }

    // 13.
    /**
     * Verifies that the exception message includes the requested datetime
     * when no weather data is found for the specified time.
     */
    @Test
    void testErrorMessageContainsDatetime() {
        LocalDateTime time = LocalDateTime.of(2026, 3, 23, 12, 0);

        when(weatherRepository
                .findTopByCityAndTimestampLessThanEqualOrderByTimestampDesc(City.TARTU, time))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(WeatherDataNotFoundException.class,
                () -> deliveryFeeService.calculate(City.TARTU, VehicleType.BIKE, time));

        String expectedDate = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        assertTrue(ex.getMessage().contains("before " + expectedDate));
    }

    // 14.
    /**
     * Verifies that delivery fee calculation correctly depends on weather data
     * at different timestamps for the same city. Ensures that the latest available
     * weather record before or at the requested time is used.
     */
    @Test
    void testCalculateFeeWithDifferentTimestamps() {
        City city = City.TARTU;

        LocalDateTime t1 = LocalDateTime.of(2026, 3, 23, 12, 0);
        LocalDateTime t2 = t1.plusMinutes(5);
        LocalDateTime t3 = t1.plusMinutes(10);

        Weather w1 = new Weather();
        w1.setCity(city);
        w1.setTemperature(5.0);
        w1.setWindSpeed(2.0);
        w1.setPhenomenon(null);
        w1.setTimestamp(t1);

        Weather w2 = new Weather();
        w2.setCity(city);
        w2.setTemperature(-2.0);
        w2.setWindSpeed(2.0);
        w2.setPhenomenon(null);
        w2.setTimestamp(t2);

        Weather w3 = new Weather();
        w3.setCity(city);
        w3.setTemperature(-15.0);
        w3.setWindSpeed(2.0);
        w3.setPhenomenon(null);
        w3.setTimestamp(t3);

        // mock behavior
        when(weatherRepository
                .findTopByCityAndTimestampLessThanEqualOrderByTimestampDesc(city, t1))
                .thenReturn(Optional.of(w1));

        when(weatherRepository
                .findTopByCityAndTimestampLessThanEqualOrderByTimestampDesc(city, t2))
                .thenReturn(Optional.of(w2));

        when(weatherRepository
                .findTopByCityAndTimestampLessThanEqualOrderByTimestampDesc(city, t3))
                .thenReturn(Optional.of(w3));

        // execute
        double fee1 = deliveryFeeService.calculate(city, VehicleType.BIKE, t1);
        double fee2 = deliveryFeeService.calculate(city, VehicleType.BIKE, t2);
        double fee3 = deliveryFeeService.calculate(city, VehicleType.BIKE, t3);

        // assert
        assertEquals(2.5, fee1); // base only
        assertEquals(3.0, fee2); // +0.5 temp
        assertEquals(3.5, fee3); // +1.0 temp
    }
}