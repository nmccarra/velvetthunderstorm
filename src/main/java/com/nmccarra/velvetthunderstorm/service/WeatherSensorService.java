package com.nmccarra.velvetthunderstorm.service;

import com.nmccarra.velvetthunderstorm.core.command.GetSensorsMetricsCommand;
import com.nmccarra.velvetthunderstorm.core.exception.NoMeasurementsFoundException;
import com.nmccarra.velvetthunderstorm.core.metrics.FloatCollectionMetrics;
import com.nmccarra.velvetthunderstorm.core.metrics.MetricValueCalculator;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurement;
import com.nmccarra.velvetthunderstorm.model.WeatherMetrics;
import com.nmccarra.velvetthunderstorm.model.WeatherSensorMetrics;
import com.nmccarra.velvetthunderstorm.repository.WeatherSensorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling weather sensor data and metrics calculations.
 */
@Service
public class WeatherSensorService {

    Logger logger = LoggerFactory.getLogger(WeatherSensorService.class);

    private final WeatherSensorRepository weatherSensorRepository;

    @Autowired
    public WeatherSensorService(WeatherSensorRepository weatherSensorRepository) {
        this.weatherSensorRepository = weatherSensorRepository;
    }

    public String addMeasurement(WeatherMeasurement measurement) {
        String id = weatherSensorRepository.saveMeasurement(measurement);
        logger.info("model=weather_sensor_service action=add_measurement status=complete, metadata={id={}, measurement={}}", id, measurement);
        return id;
    }

    public List<WeatherSensorMetrics> calculateSensorsMetrics(GetSensorsMetricsCommand command) {
        List<WeatherMeasurement> measurements = weatherSensorRepository.getSensorsMeasurements(
                command.sensorIds(),
                command.measurementTypes(),
                command.fromDateTime(),
                command.toDateTime()
        );

        if (measurements.isEmpty()) {
            logger.info("model=weather_sensor_service action=calculate_sensors_metrics status=no_measurements_found, metadata={command={}}", command);
            throw new NoMeasurementsFoundException("No measurements found for sensor ids: " + command.sensorIds());
        }

        List<WeatherSensorMetrics> weatherSensorMetrics = command.sensorIds().stream()
                .map(id -> {
                    List<WeatherMeasurement> currentSensorMeasurements = measurements.stream()
                            .filter(m -> m.sensorId().equals(id))
                            .toList();

                    List<WeatherMetrics> weatherMetrics =
                            command.measurementTypes().stream().map(type -> {
                                List<Float> values = currentSensorMeasurements.stream()
                                        .filter(m -> m.measurementType() == type)
                                        .map(WeatherMeasurement::measurementValue)
                                        .toList();

                                FloatCollectionMetrics floatCollectionMetrics = MetricValueCalculator.calculateAllMetrics(values);

                                return new WeatherMetrics(
                                        type,
                                        floatCollectionMetrics.minimum(),
                                        floatCollectionMetrics.maximum(),
                                        floatCollectionMetrics.average(),
                                        values.size()
                                );
                            }).toList();

                    return new WeatherSensorMetrics(
                            id,
                            command.fromDateTime(),
                            command.toDateTime(),
                            weatherMetrics
                    );
                }).toList();
        logger.info("model=weather_sensor_service action=calculate_sensors_metrics status=complete, metadata={command={}, metrics={}}", command, weatherSensorMetrics);
        return weatherSensorMetrics;
    }
}
