package com.nmccarra.velvetthunderstorm.model;

import java.time.LocalDateTime;

public record WeatherMeasurement(String sensorId, WeatherMeasurementType measurementType, Float measurementValue,
                                 LocalDateTime createdAt) {

    public static WeatherMeasurement from(
            String sensorId,
            WeatherMeasurementRep rep
    ) {
        LocalDateTime createdAt;

        if (rep.getCreatedAt() == null) {
            createdAt = LocalDateTime.now();
        } else {
            createdAt = LocalDateTime.from(rep.getCreatedAt());
        }

        return new WeatherMeasurement(
                sensorId,
                WeatherMeasurementType.valueOf(rep.getMeasurementType().name()),
                rep.getMeasurementValue(),
                createdAt
        );
    }
}
