package com.nmccarra.velvetthunderstorm.core.command;

import com.nmccarra.velvetthunderstorm.core.exception.NoSensorIdsProvidedException;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

public record GetSensorsMetricsCommand(
        List<String> sensorIds,
        List<WeatherMeasurementType> measurementTypes,
        LocalDateTime fromDateTime,
        LocalDateTime toDateTime
) {

    public static GetSensorsMetricsCommand of(
            List<String> sensorIds,
            List<WeatherMeasurementTypeRep> measurementTypes,
            String fromDateTime,
            String toDateTime
    ) {
        LocalDateTime from = null;
        LocalDateTime to = null;

        if (sensorIds == null || sensorIds.isEmpty()) {
            throw new NoSensorIdsProvidedException("At least one sensorId must be provided");
        }

        if (measurementTypes == null || measurementTypes.isEmpty()) {
            measurementTypes = Arrays.stream(WeatherMeasurementTypeRep.values()).toList();
        }

        if(fromDateTime != null) {
            from = OffsetDateTime.of(LocalDateTime.parse(fromDateTime), ZoneOffset.UTC).toLocalDateTime();
        }

        if(toDateTime != null) {
            to = OffsetDateTime.of(LocalDateTime.parse(toDateTime), ZoneOffset.UTC).toLocalDateTime();
        }

        List<WeatherMeasurementType> mts = measurementTypes.stream()
                .map(mt -> WeatherMeasurementType.valueOf(mt.name()))
                .toList();

        return new GetSensorsMetricsCommand(
                sensorIds,
                mts,
                from,
                to
        );
    }
}
