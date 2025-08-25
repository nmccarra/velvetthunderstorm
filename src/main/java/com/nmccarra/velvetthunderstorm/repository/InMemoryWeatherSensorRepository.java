package com.nmccarra.velvetthunderstorm.repository;

import com.nmccarra.velvetthunderstorm.entity.InMemoryWeatherMeasurementEntity;
import com.nmccarra.velvetthunderstorm.entity.WeatherMeasurementEntity;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType;
import com.nmccarra.velvetthunderstorm.model.WeatherSensorMetrics;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurement;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * In-memory implementation of the WeatherSensorRepository for testing and development purposes.
 */
@Repository
public class InMemoryWeatherSensorRepository implements WeatherSensorRepository {

    List<WeatherMeasurementEntity> measurements;
    List<WeatherSensorMetrics> metrics;

    public InMemoryWeatherSensorRepository() {
        this.measurements = new java.util.ArrayList<>();
        this.metrics = new java.util.ArrayList<>();
    }

    @Override
    public List<WeatherMeasurement> getSensorsMeasurements(
            List<String> sensorIds,
            List<WeatherMeasurementType> measurementTypes,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime
    ) {
        return measurements.stream().filter(m -> sensorIds.contains(m.getSensorId()))
                .filter(m -> measurementTypes.contains(m.getMeasurementType()))
                .filter(m -> {
                    LocalDateTime measurementDateTime = m.getCreatedAt();
                    return (fromDateTime == null || measurementDateTime.isAfter(fromDateTime) || measurementDateTime.equals(fromDateTime))
                            && (toDateTime == null || measurementDateTime.isBefore(toDateTime) || measurementDateTime.equals(toDateTime));
                })
                .map(m -> ((InMemoryWeatherMeasurementEntity) m).toWeatherMeasurement())
                .toList();
    }

    @Override
    public String saveMeasurement(WeatherMeasurement measurement) {
        InMemoryWeatherMeasurementEntity entity =
                new InMemoryWeatherMeasurementEntity(
                        measurement.sensorId(),
                        measurement.measurementType(),
                        measurement.measurementValue(),
                        measurement.createdAt()
                );
        measurements.add(entity);
        return entity.getId();
    }

    @Override
    public void clearAllMeasurements() {
        this.measurements.clear();
        this.metrics.clear();
    }
}
