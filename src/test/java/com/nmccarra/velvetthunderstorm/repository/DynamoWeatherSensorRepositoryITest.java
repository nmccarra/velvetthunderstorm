package com.nmccarra.velvetthunderstorm.repository;

import com.nmccarra.velvetthunderstorm.BaseIntegrationTest;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurement;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@SpringBootTest
public class DynamoWeatherSensorRepositoryITest extends BaseIntegrationTest {

    @Autowired
    private WeatherSensorRepository weatherSensorRepository;

    @Test
    @DisplayName("Add a weather measurement successfully")
    void addMeasurement() {
        LocalDateTime now = LocalDateTime.now();

        WeatherMeasurement measurement = new WeatherMeasurement(
                "1",
                WeatherMeasurementType.TEMPERATURE,
                22.5f,
                now
        );

        String id = weatherSensorRepository.saveMeasurement(measurement);

        var measurements = weatherSensorRepository.getSensorsMeasurements(
                java.util.List.of("1"),
                java.util.List.of(WeatherMeasurementType.TEMPERATURE),
                null,
                null
        );

        Assertions.assertEquals("1-TEMPERATURE-" + now.toEpochSecond(ZoneOffset.UTC), id);
        Assertions.assertEquals("1", measurements.getFirst().sensorId());
        Assertions.assertEquals(WeatherMeasurementType.TEMPERATURE, measurements.getFirst().measurementType());
        Assertions.assertEquals(22.5f, measurements.getFirst().measurementValue());
        Assertions.assertNotNull(measurements.getFirst().createdAt());
    }

    @Test
    @DisplayName("Query weather measurements by sensor ID and measurement type")
    void queryMeasurementsBySensorIdAndType() {
        LocalDateTime now = LocalDateTime.now();

        WeatherMeasurement measurement1 = new WeatherMeasurement(
                "2",
                WeatherMeasurementType.HUMIDITY,
                55.0f,
                now.minusHours(1)
        );

        WeatherMeasurement measurement2 = new WeatherMeasurement(
                "2",
                WeatherMeasurementType.TEMPERATURE,
                20.0f,
                now.minusHours(2)
        );

        WeatherMeasurement measurement3 = new WeatherMeasurement(
                "2",
                WeatherMeasurementType.HUMIDITY,
                20.0f,
                now.minusHours(2)
        );

        weatherSensorRepository.saveMeasurement(measurement1);
        weatherSensorRepository.saveMeasurement(measurement2);
        weatherSensorRepository.saveMeasurement(measurement3);

        var measurements = weatherSensorRepository.getSensorsMeasurements(
                java.util.List.of("2"),
                java.util.List.of(WeatherMeasurementType.HUMIDITY, WeatherMeasurementType.TEMPERATURE),
                now.minusHours(1),
                now
        );

        Assertions.assertEquals(1, measurements.size());
        Assertions.assertEquals("2", measurements.getFirst().sensorId());
        Assertions.assertEquals(WeatherMeasurementType.HUMIDITY, measurements.getFirst().measurementType());
        Assertions.assertEquals(55.0f, measurements.getFirst().measurementValue());
    }
}
