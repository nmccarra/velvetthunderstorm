package com.nmccarra.velvetthunderstorm.model;

import java.util.List;

public record WeatherSensorMetrics(
        String sensorId,
        java.time.LocalDateTime fromDateTime,
        java.time.LocalDateTime toDateTime,
        List<WeatherMetrics> weatherMetrics
) {

    public List<WeatherMeasurementType> getWeatherMeasurementTypes() {
        return weatherMetrics().stream()
                .map(WeatherMetrics::measurementType)
                .toList();
    }

    public WeatherSensorMetricsRep toRep() {
        WeatherSensorMetricsRep rep = new WeatherSensorMetricsRep();
        rep.setSensorId(this.sensorId);
        if (fromDateTime != null) {
            rep.setFromDateTime(this.fromDateTime.toString());
        }
        if (toDateTime != null) {
            rep.setToDateTime(this.toDateTime.toString());
        }

        weatherMetrics.forEach(
            metrics -> rep.addMetricsItem(metrics.toRep())
        );

        return rep;
    }
}
