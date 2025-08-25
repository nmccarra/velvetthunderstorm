package com.nmccarra.velvetthunderstorm.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeatherMeasurementTest {

    @Test
    @DisplayName("should create WeatherMeasurement from WeatherMeasurementRep")
    void from() {
        WeatherMeasurementRep rep = new WeatherMeasurementRep();
        rep.setMeasurementType(WeatherMeasurementTypeRep.TEMPERATURE);
        rep.setMeasurementValue(20.5f);
        rep.setCreatedAt(java.time.OffsetDateTime.of(2024, 1, 1, 12, 30, 0, 0, java.time.ZoneOffset.UTC));

        WeatherMeasurement measurement = WeatherMeasurement.from("1", rep);
        assertEquals("1", measurement.sensorId());
        assertEquals(WeatherMeasurementType.TEMPERATURE, measurement.measurementType());
        assertEquals(20.5f, measurement.measurementValue());
        assertEquals(java.time.LocalDateTime.of(2024, 1, 1, 12, 30), measurement.createdAt());
    }

    @Test
    @DisplayName("should default to current time when createdAt is null")
    void from_missingCreatedAt() {
        WeatherMeasurementRep rep = new WeatherMeasurementRep();
        rep.setMeasurementType(WeatherMeasurementTypeRep.TEMPERATURE);
        rep.setMeasurementValue(20.5f);
        rep.setCreatedAt(null);

        WeatherMeasurement measurement = WeatherMeasurement.from("1", rep);
        assertEquals("1", measurement.sensorId());
        assertEquals(WeatherMeasurementType.TEMPERATURE, measurement.measurementType());
        assertEquals(20.5f, measurement.measurementValue());
        assertNotNull(measurement.createdAt());
    }
}
