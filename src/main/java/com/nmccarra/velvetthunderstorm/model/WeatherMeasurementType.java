package com.nmccarra.velvetthunderstorm.model;

public enum WeatherMeasurementType {
    TEMPERATURE,
    HUMIDITY,
    WIND_SPEED,
    AIR_PRESSURE,
    PRECIPITATION;

    public WeatherMeasurementTypeRep toRep() {
        return WeatherMeasurementTypeRep.valueOf(this.name());
    }
}
