package com.nmccarra.velvetthunderstorm.core.metrics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricValueCalculatorTest {

    @Test
    @DisplayName("should calculate all metrics for a list of float values")
    void calculateAllMetrics() {
        FloatCollectionMetrics metrics = MetricValueCalculator.calculateAllMetrics(
                java.util.List.of(1.0f, 2.0f, 3.0f, 4.0f, 5.0f)
        );

        assertNotNull(metrics);
        assertEquals(3.0f, metrics.average());
        assertEquals(1.0f, metrics.minimum());
        assertEquals(5.0f, metrics.maximum());
    }

    @Test
    @DisplayName("should return null metrics for an empty list")
    void calculateAllMetrics_emptyList() {
        FloatCollectionMetrics metrics = MetricValueCalculator.calculateAllMetrics(
                java.util.List.of()
        );

        assertNotNull(metrics);
        assertNull(metrics.average());
        assertNull(metrics.minimum());
        assertNull(metrics.maximum());
    }
}
