package com.nmccarra.velvetthunderstorm.controller;

import com.nmccarra.velvetthunderstorm.BaseIntegrationTest;
import com.nmccarra.velvetthunderstorm.model.ErrorRep;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurementRep;
import com.nmccarra.velvetthunderstorm.model.WeatherSensorMetrics;
import com.nmccarra.velvetthunderstorm.model.WeatherSensorMetricsRep;
import com.nmccarra.velvetthunderstorm.repository.WeatherSensorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WeatherSensorControllerITest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WeatherSensorRepository weatherSensorRepository;


    @Nested
    class AddSensorWeatherVariableMeasurement {

        @Test
        @DisplayName("should add measurement successfully")
        void shouldAddMeasurementSuccessfully() {
            // Given
            String sensorId = "1";
            OffsetDateTime createdAt = OffsetDateTime.parse("2024-06-01T12:00:00Z");

            var measurementRep = new com.nmccarra.velvetthunderstorm.model.WeatherMeasurementRep();
            measurementRep.setMeasurementType(com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep.TEMPERATURE);
            measurementRep.setMeasurementValue(22.5f);
            measurementRep.setCreatedAt(createdAt);

            // When
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    "http://localhost:" + port + "/v1/sensors/" + sensorId + "/weather-variable-measurement",
                    measurementRep,
                    Void.class
            );
            assertEquals(201, response.getStatusCode().value());

            String expectedId = sensorId + "-" + measurementRep.getMeasurementType().toString() + "-" + createdAt.toEpochSecond();
            assertEquals(expectedId, response.getHeaders().getLocation().toString());
        }

        @Test
        @DisplayName("should return 400 for invalid measurement type")
        void shouldReturn400ForInvalidMeasurementType() {
            // Given
            String sensorId = "1";
            OffsetDateTime createdAt = OffsetDateTime.parse("2024-06-01T12:00:00Z");

            var measurementRep = new com.nmccarra.velvetthunderstorm.model.WeatherMeasurementRep();
            measurementRep.setMeasurementType(null); // Invalid type
            measurementRep.setMeasurementValue(-300f);
            measurementRep.setCreatedAt(createdAt);

            // When
            ResponseEntity<ErrorRep> response = restTemplate.postForEntity(
                    "http://localhost:" + port + "/v1/sensors/" + sensorId + "/weather-variable-measurement",
                    measurementRep,
                    ErrorRep.class
            );

            assertEquals(400, response.getStatusCode().value());
            assertEquals("measurementType: must not be null", response.getBody().getErrors().get(0));
        }
    }

    @Nested
    class GetSensorMetrics {

        @BeforeEach
        void setup() {
            addMeasurements("1");
        }

        @AfterEach
        void teardown() {
            weatherSensorRepository.clearAllMeasurements();
        }

        @Test
        @DisplayName("should get sensor metrics successfully")
        void shouldGetSensorMetricsSuccessfully() {

            String sensorId = "1";
            String fromDateTime = "2024-06-01T00:00:00";
            String toDateTime = "2024-06-02T00:00:00";
            String measurementTypes = "TEMPERATURE,HUMIDITY";

            // When
            ResponseEntity<com.nmccarra.velvetthunderstorm.model.WeatherSensorMetricsRep> response = restTemplate.getForEntity(
                    "http://localhost:" + port + "/v1/sensors/" + sensorId + "/metrics?fromDateTime=" + fromDateTime + "&toDateTime=" + toDateTime + "&measurementTypes=" + measurementTypes,
                    com.nmccarra.velvetthunderstorm.model.WeatherSensorMetricsRep.class
            );

            // Then
            assertEquals(200, response.getStatusCode().value());
            var metricsRep = response.getBody();
            assert metricsRep != null;
            assertEquals(sensorId, metricsRep.getSensorId());

            var temperatureMetrics = metricsRep.getMetrics().stream()
                    .filter(m -> m.getMeasurementType() == com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep.TEMPERATURE)
                    .findFirst()
                    .orElseThrow();

            assertEquals(22.5f, temperatureMetrics.getAverage());
            assertEquals(20.0f, temperatureMetrics.getMin());
            assertEquals(25.0f, temperatureMetrics.getMax());
            assertEquals(2, temperatureMetrics.getCount());

            var humidityMetrics = metricsRep.getMetrics().stream()
                    .filter(m -> m.getMeasurementType() == com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep.HUMIDITY)
                    .findFirst()
                    .orElseThrow();

            assertEquals(60.0f, humidityMetrics.getAverage());
            assertEquals(60.0f, humidityMetrics.getMin());
            assertEquals(60.0f, humidityMetrics.getMax());
            assertEquals(1, humidityMetrics.getCount());
        }

        @Test
        @DisplayName("should return 404 when no measurements found")
        void shouldReturn404WhenNoMeasurementsFound() {
            // Given
            String sensorId = "999"; // Assuming this sensor has no measurements
            String fromDateTime = "2024-06-01T00:00:00";
            String toDateTime = "2024-06-02T00:00:00";
            String measurementTypes = "TEMPERATURE";

            // When
            ResponseEntity<ErrorRep> response = restTemplate.getForEntity(
                    "http://localhost:" + port + "/v1/sensors/" + sensorId + "/metrics?fromDateTime=" + fromDateTime + "&toDateTime=" + toDateTime + "&measurementTypes=" + measurementTypes,
                    ErrorRep.class
            );

            assertEquals(404, response.getStatusCode().value());
            assertEquals("No measurements found for sensor ids: [999]", response.getBody().getErrors().get(0));
        }

        @Test
        @DisplayName("should return 400 for invalid date range")
        void shouldReturn400ForInvalidDateRange() {
            // Given
            String sensorId = "1";
            String fromDateTime = "2024-06-02T00:00:00";
            String toDateTime = "2024-06-01T00:00:0"; // Invalid range
            String measurementTypes = "TEMPERATURE";

            // When
            ResponseEntity<ErrorRep> response = restTemplate.getForEntity(
                    "http://localhost:" + port + "/v1/sensors/" + sensorId + "/metrics?fromDateTime=" + fromDateTime + "&toDateTime=" + toDateTime + "&measurementTypes=" + measurementTypes,
                    ErrorRep.class
            );
            assertEquals(400, response.getStatusCode().value());
        }

        @Test
        @DisplayName("should return 400 for invalid measurement type")
        void shouldReturn400ForInvalidMeasurementType() {
            // Given
            String sensorId = "1";
            String fromDateTime = "2024-06-01T00:00:00";
            String toDateTime = "2024-06-02T00:00:00";
            String measurementTypes = "INVALID_TYPE"; // Invalid type

            // When
            ResponseEntity<ErrorRep> response = restTemplate.getForEntity(
                    "http://localhost:" + port + "/v1/sensors/" + sensorId + "/metrics?fromDateTime=" + fromDateTime + "&toDateTime=" + toDateTime + "&measurementTypes=" + measurementTypes,
                    ErrorRep.class
            );

            assertEquals(400, response.getStatusCode().value());
            assertEquals("Invalid value for parameter 'measurementTypes': INVALID_TYPE", response.getBody().getErrors().get(0));
        }
    }

    @Nested
    class GetSensorsMetrics {

        @BeforeEach
        void setup() {
            addMeasurements("1");
            addMeasurements("2");
        }

        @AfterEach
        void teardown() {
            weatherSensorRepository.clearAllMeasurements();
        }

        @Test
        @DisplayName("should get sensors metrics successfully")
        void shouldGetSensorsMetricsSuccessfully() {

            var request = new com.nmccarra.velvetthunderstorm.model.WeatherSensorsMetricsRequest();
            request.setSensorIds(List.of("1", "2"));

            String fromDateTime = "2024-06-01T00:00:00";
            String toDateTime = "2024-06-02T00:00:00";
            String measurementTypes = "TEMPERATURE,HUMIDITY";

            ResponseEntity<List<WeatherSensorMetricsRep>> response = restTemplate.postForEntity(
                    "http://localhost:" + port + "/v1/sensors/metrics?fromDateTime=" + fromDateTime + "&toDateTime=" + toDateTime + "&measurementTypes=" + measurementTypes,
                    request,
                    (Class<List<WeatherSensorMetricsRep>>) (Class<?>) List.class
            );

            assertEquals(200, response.getStatusCode().value());
            var metricsReps = response.getBody();
            assert metricsReps != null;
            assertEquals(2, metricsReps.size());
        }

        @Test
        @DisplayName("should return 400 for empty sensor ids")
        void shouldReturn400ForEmptySensorIds() {
            var request = new com.nmccarra.velvetthunderstorm.model.WeatherSensorsMetricsRequest();
            request.setSensorIds(List.of()); // Empty list

            String fromDateTime = "2024-06-01T00:00:00";
            String toDateTime = "2024-06-02T00:00:00";
            String measurementTypes = "TEMPERATURE";

            ResponseEntity<ErrorRep> response = restTemplate.postForEntity(
                    "http://localhost:" + port + "/v1/sensors/metrics?fromDateTime=" + fromDateTime + "&toDateTime=" + toDateTime + "&measurementTypes=" + measurementTypes,
                    request,
                    ErrorRep.class
            );

            assertEquals(400, response.getStatusCode().value());
            assertEquals("At least one sensorId must be provided", response.getBody().getErrors().get(0));
        }

        @Test
        @DisplayName("should return 400 for invalid measurement type")
        void shouldReturn400ForInvalidMeasurementType() {
            var request = new com.nmccarra.velvetthunderstorm.model.WeatherSensorsMetricsRequest();
            request.setSensorIds(List.of("1", "2"));

            String fromDateTime = "2024-06-01T00:00:00";
            String toDateTime = "2024-06-02T00:00:00";
            String measurementTypes = "INVALID_TYPE"; // Invalid type

            ResponseEntity<ErrorRep> response = restTemplate.postForEntity(
                    "http://localhost:" + port + "/v1/sensors/metrics?fromDateTime=" + fromDateTime + "&toDateTime=" + toDateTime + "&measurementTypes=" + measurementTypes,
                    request,
                    ErrorRep.class
            );
            assertEquals(400, response.getStatusCode().value());
            assertEquals("Invalid value for parameter 'measurementTypes': INVALID_TYPE", response.getBody().getErrors().get(0));
        }

        @Test
        @DisplayName("should return 400 for invalid date range")
        void shouldReturn400ForInvalidDateRange() {
            var request = new com.nmccarra.velvetthunderstorm.model.WeatherSensorsMetricsRequest();
            request.setSensorIds(List.of("1", "2"));

            String fromDateTime = "2024-06-02T00:00:00";
            String toDateTime = "2024-06-01T00:00:0"; // Invalid range
            String measurementTypes = "TEMPERATURE";

            ResponseEntity<ErrorRep> response = restTemplate.postForEntity(
                    "http://localhost:" + port + "/v1/sensors/metrics?fromDateTime=" + fromDateTime + "&toDateTime=" + toDateTime + "&measurementTypes=" + measurementTypes,
                    request,
                    ErrorRep.class
            );

            assertEquals(400, response.getStatusCode().value());
        }

        @Test
        @DisplayName("should return 404 when no measurements found")
        void shouldReturn404WhenNoMeasurementsFound() {
            var request = new com.nmccarra.velvetthunderstorm.model.WeatherSensorsMetricsRequest();
            request.setSensorIds(List.of("999")); // Assuming this sensor has no measurements

            String fromDateTime = "2024-06-01T00:00:00";
            String toDateTime = "2024-06-02T00:00:00";
            String measurementTypes = "TEMPERATURE";

            ResponseEntity<ErrorRep> response = restTemplate.postForEntity(
                    "http://localhost:" + port + "/v1/sensors/metrics?fromDateTime=" + fromDateTime + "&toDateTime=" + toDateTime + "&measurementTypes=" + measurementTypes,
                    request,
                    ErrorRep.class
            );
            assertEquals(404, response.getStatusCode().value());
            assertEquals("No measurements found for sensor ids: [999]", response.getBody().getErrors().get(0));
        }
    }

    public void addMeasurements(String sensorId) {
        var measurement1 = new com.nmccarra.velvetthunderstorm.model.WeatherMeasurementRep();
        measurement1.setMeasurementType(com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep.TEMPERATURE);
        measurement1.setMeasurementValue(20.0f);
        measurement1.setCreatedAt(OffsetDateTime.parse("2024-06-01T01:00:00Z"));

        var measurement2 = new com.nmccarra.velvetthunderstorm.model.WeatherMeasurementRep();
        measurement2.setMeasurementType(com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep.HUMIDITY);
        measurement2.setMeasurementValue(60.0f);
        measurement2.setCreatedAt(OffsetDateTime.parse("2024-06-01T02:00:00Z"));

        var measurement3 = new com.nmccarra.velvetthunderstorm.model.WeatherMeasurementRep();
        measurement3.setMeasurementType(com.nmccarra.velvetthunderstorm.model.WeatherMeasurementTypeRep.TEMPERATURE);
        measurement3.setMeasurementValue(25.0f);
        measurement3.setCreatedAt(OffsetDateTime.parse("2024-06-01T03:00:00Z"));

        List<WeatherMeasurementRep> reps = List.of(measurement1, measurement2, measurement3);

        reps.forEach(
                rep -> restTemplate.postForEntity(
                        "http://localhost:" + port + "/v1/sensors/" + sensorId + "/weather-variable-measurement",
                        rep,
                        Void.class
                )
        );
    }
}
