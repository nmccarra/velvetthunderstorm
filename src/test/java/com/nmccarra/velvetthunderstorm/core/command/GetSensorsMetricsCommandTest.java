package com.nmccarra.velvetthunderstorm.core.command;

import com.nmccarra.velvetthunderstorm.core.exception.NoSensorIdsProvidedException;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetSensorsMetricsCommandTest {

    @Test
    @DisplayName("should create command with valid parameters")
    void of() {
        GetSensorsMetricsCommand command = GetSensorsMetricsCommand.of(
                List.of("1", "2"),
                List.of(
                        com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep.TEMPERATURE,
                        com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep.HUMIDITY
                ),
                "2024-01-01T00:00:00",
                "2024-01-02T00:00:00"
        );

        assertIterableEquals(List.of("1", "2"), command.sensorIds());
        assertIterableEquals(List.of(WeatherMeasurementType.TEMPERATURE, WeatherMeasurementType.HUMIDITY), command.measurementTypes());
        assertEquals(LocalDateTime.of(2024, 1, 1, 0 ,0), command.fromDateTime());
        assertEquals(LocalDateTime.of(2024, 1, 2, 0 ,0), command.toDateTime());
    }

    @Test
    @DisplayName("should throw exception when sensorIds is null")
    void of_nullSensorIds() {
        NoSensorIdsProvidedException exception = assertThrows(NoSensorIdsProvidedException.class, () -> {
            GetSensorsMetricsCommand.of(
                    null,
                    List.of(com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep.TEMPERATURE),
                    "2024-01-01T00:00:00",
                    "2024-01-02T00:00:00"
            );
        });
        assertEquals("At least one sensorId must be provided", exception.getMessage());
    }

    @Test
    @DisplayName("should throw exception when sensorIds is empty")
    void of_emptySensorIds() {
        NoSensorIdsProvidedException exception = assertThrows(NoSensorIdsProvidedException.class, () -> {
            GetSensorsMetricsCommand.of(
                    List.of(),
                    List.of(com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep.TEMPERATURE),
                    "2024-01-01T00:00:00",
                    "2024-01-02T00:00:00"
            );
        });
        assertEquals("At least one sensorId must be provided", exception.getMessage());
    }

    @Test
    @DisplayName("should default measurementTypes when null")
    void of_nullMeasurementTypes() {
        GetSensorsMetricsCommand command = GetSensorsMetricsCommand.of(
                List.of("1"),
                null,
                "2024-01-01T00:00:00",
                "2024-01-02T00:00:00"
        );

        assertIterableEquals(List.of(WeatherMeasurementType.values()), command.measurementTypes());
    }

    @Test
    @DisplayName("should default measurementTypes when empty")
    void of_emptyMeasurementTypes() {
        GetSensorsMetricsCommand command = GetSensorsMetricsCommand.of(
                List.of("1"),
                List.of(),
                "2024-01-01T00:00:00",
                "2024-01-02T00:00:00"
        );

        assertIterableEquals(List.of(WeatherMeasurementType.values()), command.measurementTypes());
    }
}
