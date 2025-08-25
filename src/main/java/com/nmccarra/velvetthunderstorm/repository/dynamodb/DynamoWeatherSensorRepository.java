package com.nmccarra.velvetthunderstorm.repository.dynamodb;

import com.nmccarra.velvetthunderstorm.entity.dynamo.DynamoWeatherMeasurementEntity;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurement;
import com.nmccarra.velvetthunderstorm.model.WeatherMeasurementType;
import com.nmccarra.velvetthunderstorm.repository.WeatherSensorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Primary
public class DynamoWeatherSensorRepository implements WeatherSensorRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final String tableName;

    @Autowired
    public DynamoWeatherSensorRepository(DynamoDbEnhancedClient enhancedClient,
                                         @Value("${aws.dynamodb.table-name}") String tableName) {
        this.dynamoDbEnhancedClient = enhancedClient;
        this.tableName = tableName;
    }

    @Override
    public List<WeatherMeasurement> getSensorsMeasurements(List<String> sensorIds, List<WeatherMeasurementType> measurementTypes, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        DynamoDbTable<DynamoWeatherMeasurementEntity> table = dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(DynamoWeatherMeasurementEntity.class));
        return sensorIds.stream().flatMap(sensorId -> {
            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder().partitionValue(sensorId).build());

            return table.query(queryConditional)
                    .items()
                    .stream()
                    .filter(m -> measurementTypes.contains(m.getMeasurementType()))
                    .filter(m -> {
                        LocalDateTime measurementDateTime = m.getCreatedAt();
                        return (fromDateTime == null || measurementDateTime.isAfter(fromDateTime) || measurementDateTime.equals(fromDateTime))
                                && (toDateTime == null || measurementDateTime.isBefore(toDateTime) || measurementDateTime.equals(toDateTime));
                    })
                    .map(DynamoWeatherMeasurementEntity::toWeatherMeasurement);
        }).toList();
    }

    @Override
    public String saveMeasurement(WeatherMeasurement measurement) {
        DynamoWeatherMeasurementEntity dynamoWeatherMeasurementEntity = DynamoWeatherMeasurementEntity.from(measurement);

        DynamoDbTable<DynamoWeatherMeasurementEntity> table = dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(DynamoWeatherMeasurementEntity.class));

        table.putItem(DynamoWeatherMeasurementEntity.from(measurement));
        return dynamoWeatherMeasurementEntity.getId();
    }

    public void createTable() {
        System.out.println("Creating table " + tableName);
        DynamoDbTable<DynamoWeatherMeasurementEntity> table = dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(DynamoWeatherMeasurementEntity.class));
        table.createTable();
    }

    @Override
    public void clearAllMeasurements() {
        DynamoDbTable<DynamoWeatherMeasurementEntity> table = dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(DynamoWeatherMeasurementEntity.class));
        table.deleteTable();
        table.createTable();
    }
}
