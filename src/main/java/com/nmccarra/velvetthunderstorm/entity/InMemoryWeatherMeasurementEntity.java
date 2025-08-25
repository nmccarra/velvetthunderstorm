package com.nmccarra.velvetthunderstorm.entity;

import com.nmccarra.velvetthunderstorm.model.WeatherMeasurement;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class InMemoryWeatherMeasurementEntity implements WeatherMeasurementEntity {

    private String id;
    private String sensorId;
    private WeatherMeasurementType measurementType;
    private Float measurementValue;
    private LocalDateTime createdAt;

    public InMemoryWeatherMeasurementEntity(
        String sensorId,
        WeatherMeasurementType measurementType,
        Float measurementValue,
        LocalDateTime createdAt
    ) {
        this.id = sensorId + "-" + measurementType + "-" + createdAt.toEpochSecond(ZoneOffset.UTC);
        this.sensorId = sensorId;
        this.measurementType = measurementType;
        this.measurementValue = measurementValue;
        this.createdAt = createdAt;
    }

    public WeatherMeasurement toWeatherMeasurement() {
        return new WeatherMeasurement(
            this.sensorId,
            this.measurementType,
            this.measurementValue,
            this.createdAt
        );
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getSensorId() {
        return sensorId;
    }

    @Override
    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public WeatherMeasurementType getMeasurementType() {
        return measurementType;
    }

    @Override
    public void setMeasurementType(WeatherMeasurementType measurementType) {
        this.measurementType = measurementType;
    }

    @Override
    public Float getMeasurementValue() {
        return measurementValue;
    }

    @Override
    public void setMeasurementValue(Float measurementValue) {
        this.measurementValue = measurementValue;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
