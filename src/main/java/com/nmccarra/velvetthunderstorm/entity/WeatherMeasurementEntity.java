package com.nmccarra.velvetthunderstorm.entity;

import com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType;

import java.time.LocalDateTime;

public interface WeatherMeasurementEntity {

    String getId();

    void setId(String id);

    String getSensorId();

    void setSensorId(String sensorId);

    WeatherMeasurementType getMeasurementType();

    void setMeasurementType(WeatherMeasurementType measurementType);

    Float getMeasurementValue();

    void setMeasurementValue(Float measurementValue);

    LocalDateTime getCreatedAt();

    void setCreatedAt(LocalDateTime createdAt);
}
