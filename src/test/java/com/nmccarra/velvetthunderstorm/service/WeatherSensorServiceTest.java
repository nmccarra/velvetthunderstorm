package com.nmccarra.velvetthunderstorm.service;

import com.nmccarra.velvetthunderstorm.core.command.GetSensorsMetricsCommand;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurement;
import com.nmccarra.velvetthunderstorm.model.WeatherSensorMetrics;
import com.nmccarra.velvetthunderstorm.repository.WeatherSensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherSensorServiceTest {

    @Mock
    private WeatherSensorRepository weatherSensorRepository;

    @InjectMocks
    private WeatherSensorService weatherSensorService;

    @Test
    @DisplayName("should add measurement and return id")
    void addMeasurement() {
        when(weatherSensorRepository.saveMeasurement(any(WeatherMeasurement.class))).thenReturn("1-TEMPERATURE-1756065020");

        WeatherMeasurement measurement = new WeatherMeasurement("1", com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType.TEMPERATURE, 20.5f, java.time.LocalDateTime.of(2024, 1, 1, 12, 30));
        String id = weatherSensorService.addMeasurement(measurement);
        assertEquals("1-TEMPERATURE-1756065020", id);
    }

    @Test
    @DisplayName("should calculate sensors metrics")
    void calculateSensorsMetrics() {
        when(weatherSensorRepository.getSensorsMeasurements(any(), any(), any(), any())).thenReturn(
                java.util.List.of(
                        new WeatherMeasurement("1", com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType.TEMPERATURE, 20.5f, java.time.LocalDateTime.of(2024, 1, 1, 12, 30)),
                        new WeatherMeasurement("1", com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType.HUMIDITY, 65.0f, java.time.LocalDateTime.of(2024, 1, 1, 12, 35)),
                        new WeatherMeasurement("2", com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType.TEMPERATURE, 22.0f, java.time.LocalDateTime.of(2024, 1, 1, 13, 0)),
                        new WeatherMeasurement("3", com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType.HUMIDITY, 70.0f, java.time.LocalDateTime.of(2024, 1, 1, 13, 5))
                )
        );

        GetSensorsMetricsCommand command = GetSensorsMetricsCommand.of(
                java.util.List.of("1", "2"),
                java.util.List.of(com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep.TEMPERATURE, com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep.HUMIDITY),
                "2024-01-01T00:00:00",
                "2024-01-02T00:00:00"
        );

        List<WeatherSensorMetrics>  metrics = weatherSensorService.calculateSensorsMetrics(command);

        assertArrayEquals(new String[]{"1", "2"}, metrics.stream().map(WeatherSensorMetrics::sensorId).toArray());
        assertEquals(LocalDateTime.parse("2024-01-01T00:00:00"), metrics.getFirst().fromDateTime());
        assertEquals(LocalDateTime.parse("2024-01-02T00:00:00"), metrics.getFirst().toDateTime());

        metrics.stream().filter(m -> Objects.equals(m.sensorId(), "1")).toList().getFirst().weatherMetrics().forEach(wm -> {
            if (wm.measurementType() == com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType.TEMPERATURE) {
                assertEquals(20.5f, wm.maxValue());
                assertEquals(20.5f, wm.minValue());
                assertEquals(20.5f, wm.averageValue());
                assertEquals(1, wm.count());
            } else if (wm.measurementType() == com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType.HUMIDITY) {
                assertEquals(65.0f, wm.maxValue());
                assertEquals(65.0f, wm.minValue());
                assertEquals(65.0f, wm.averageValue());
                assertEquals(1, wm.count());
            } else {
                fail("Unexpected measurement type");
            }
        });

        metrics.stream().filter(m -> Objects.equals(m.sensorId(), "2")).toList().getFirst().weatherMetrics().forEach(wm -> {
            if (wm.measurementType() == com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType.TEMPERATURE) {
                assertEquals(22.0f, wm.maxValue());
                assertEquals(22.0f, wm.minValue());
                assertEquals(22.0f, wm.averageValue());
                assertEquals(1, wm.count());
            } else if (wm.measurementType() == com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType.HUMIDITY) {
                assertNull(wm.maxValue());
                assertNull(wm.minValue());
                assertNull(wm.averageValue());
                assertEquals(0, wm.count());
            } else {
                fail("Unexpected measurement type");
            }
        });
    }

    @Test
    @DisplayName("should throw NoMeasurementsFoundException when no measurements found")
    void calculateSensorsMetrics_noMeasurements() {
        when(weatherSensorRepository.getSensorsMeasurements(any(), any(), any(), any())).thenReturn(List.of());

        GetSensorsMetricsCommand command = GetSensorsMetricsCommand.of(
                java.util.List.of("1", "2"),
                java.util.List.of(com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep.TEMPERATURE, com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep.HUMIDITY),
                "2024-01-01T00:00:00",
                "2024-01-02T00:00:00"
        );

        assertThrows(com.nmccarra.velvetthunderstorm.core.exception.NoMeasurementsFoundException.class, () -> {
            weatherSensorService.calculateSensorsMetrics(command);
        });
    }
}
