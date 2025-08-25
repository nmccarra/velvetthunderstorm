package com.nmccarra.velvetthunderstorm.entity.dynamo;

import com.nmccarra.velvetthunderstorm.entity.WeatherMeasurementEntity;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurement;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class DynamoWeatherMeasurementEntity implements WeatherMeasurementEntity {
    private String id;
    private String PK;
    private String SK;
    private String sensorId;
    private WeatherMeasurementType measurementType;
    private Float measurementValue;
    private LocalDateTime createdAt;

    @DynamoDbPartitionKey
    public String getPK() {
        return sensorId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSK() {
        return measurementType.name() + "#" + createdAt.toEpochSecond(ZoneOffset.UTC);
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

    public static DynamoWeatherMeasurementEntity from(WeatherMeasurement measurement) {
        return new DynamoWeatherMeasurementEntity(
                measurement.sensorId() + "-" + measurement.measurementType() + "-" + measurement.createdAt().toEpochSecond(ZoneOffset.UTC),
                measurement.sensorId(),
                measurement.measurementType().name() + "#" + measurement.createdAt().toEpochSecond(ZoneOffset.UTC),
                measurement.sensorId(),
                measurement.measurementType(),
                measurement.measurementValue(),
                measurement.createdAt()
        );
    }

    public WeatherMeasurement toWeatherMeasurement() {
        return new WeatherMeasurement(
                this.sensorId,
                this.measurementType,
                this.measurementValue,
                this.createdAt
        );
    }
}
