package com.nmccarra.velvetthunderstorm.controller;


import com.nmccarra.velvetthunderstorm.api.SensorApi;
import com.nmccarra.velvetthunderstorm.core.command.GetSensorsMetricsCommand;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurement;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurementRep;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep;
import com.nmccarra.velvetthunderstorm.model.WeatherSensorMetrics;
import com.nmccarra.velvetthunderstorm.model.WeatherSensorMetricsRep;
import com.nmccarra.velvetthunderstorm.model.WeatherSensorsMetricsRequest;
import com.nmccarra.velvetthunderstorm.service.WeatherSensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
public class WeatherSensorController implements SensorApi {

    private final WeatherSensorService weatherSensorService;

    @Autowired
    public WeatherSensorController(WeatherSensorService weatherSensorService) {
        this.weatherSensorService = weatherSensorService;
    }


    @Override
    public ResponseEntity<Void> addSensorWeatherVariableMeasurement(
            String sensorId,
            WeatherMeasurementRep weatherVariableMeasurementRep
    ) {
        String id = weatherSensorService.addMeasurement(
                WeatherMeasurement.from(
                        sensorId,
                        weatherVariableMeasurementRep
                )
        );

        return ResponseEntity.created(URI.create(id)).build();
    }

    @Override
    public ResponseEntity<WeatherSensorMetricsRep> getSensorMetrics(
            String sensorId,
            String fromDateTime,
            String toDateTime,
            List<WeatherMeasurementTypeRep> measurementTypes
    ) {
        GetSensorsMetricsCommand command = GetSensorsMetricsCommand.of(
                List.of(sensorId),
                measurementTypes,
                fromDateTime,
                toDateTime
        );

        List<WeatherSensorMetrics> metrics = weatherSensorService.calculateSensorsMetrics(command);

        WeatherSensorMetricsRep rep = metrics.stream().findFirst().get().toRep();
        return ResponseEntity.ok(rep);
    }

    @Override
    public ResponseEntity<List<WeatherSensorMetricsRep>> getSensorsMetrics(
            WeatherSensorsMetricsRequest weatherSensorsMetricsRequest,
            String fromDateTime,
            String toDateTime,
            List<WeatherMeasurementTypeRep> measurementTypes
    ) {

        GetSensorsMetricsCommand command = GetSensorsMetricsCommand.of(
                weatherSensorsMetricsRequest.getSensorIds(),
                measurementTypes,
                fromDateTime,
                toDateTime
        );

        List<WeatherSensorMetrics> metrics = weatherSensorService.calculateSensorsMetrics(command);

        List<WeatherSensorMetricsRep> metricReps = metrics.stream().map(WeatherSensorMetrics::toRep).toList();
        return ResponseEntity.ok(metricReps);
    }
}


