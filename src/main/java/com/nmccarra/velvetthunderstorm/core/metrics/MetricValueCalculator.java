package com.nmccarra.velvetthunderstorm.core.metrics;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class MetricValueCalculator {

    public static FloatCollectionMetrics calculateAllMetrics(List<Float> values) {
        if (values.isEmpty()) {
            return new FloatCollectionMetrics(null, null, null);
        }

        Map<FloatMetricOperation, Float> calcMap = Arrays.stream(FloatMetricOperation.values())
                .map(floatMetricOperation -> Map.entry(floatMetricOperation, calculateMetricValue(floatMetricOperation, values))
                )
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new FloatCollectionMetrics(
                calcMap.get(FloatMetricOperation.AVERAGE),
                calcMap.get(FloatMetricOperation.MINIMUM),
                calcMap.get(FloatMetricOperation.MAXIMUM)
        );
    }


    private static Float calculateMetricValue(FloatMetricOperation operation, List<Float> values) {
        return switch (operation) {
            case AVERAGE ->
            {
                float sum = values.stream()
                        .reduce(0.0f, Float::sum);
                    yield sum / values.size();
            }
            case MINIMUM ->
                    values.stream().min(Float::compareTo).orElseThrow();
            case MAXIMUM ->
                    values.stream().max(Float::compareTo).orElseThrow();
        };
    }
}
