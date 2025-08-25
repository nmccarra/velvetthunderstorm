package com.nmccarra.velvetthunderstorm.repository;

import com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType;
import com.nmccarra.velvetthunderstorm.model.WeatherSensorMetrics;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurement;

import java.time.LocalDateTime;
import java.util.List;

public interface WeatherSensorRepository {

    List<WeatherMeasurement> getSensorsMeasurements(
            List<String> sensorIds,
            List<WeatherMeasurementType> measurementTypes,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime
    );

    String saveMeasurement(WeatherMeasurement measurement);

    void clearAllMeasurements();
}
