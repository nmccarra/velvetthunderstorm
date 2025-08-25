package com.nmccarra.velvetthunderstorm.model;

public record WeatherMetrics(WeatherMeasurementType measurementType, Float minValue, Float maxValue,
                             Float averageValue, Integer count) {

    public WeatherMetricsRep toRep() {
        WeatherMetricsRep rep = new WeatherMetricsRep();
        rep.setMeasurementType(this.measurementType.toRep());
        rep.setMin(this.minValue);
        rep.setMax(this.maxValue);
        rep.setAverage(this.averageValue);
        rep.setCount(this.count);
        return rep;
    }
}
