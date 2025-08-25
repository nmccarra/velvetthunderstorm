package com.nmccarra.velvetthunderstorm;


import com.nmccarra.velvetthunderstorm.repository.dynamodb.DynamoWeatherSensorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProfileSpecificInitializer {

    Logger logger = LoggerFactory.getLogger(ProfileSpecificInitializer.class);

    private final DynamoWeatherSensorRepository dynamoWeatherSensorRepository;

    @Autowired
    public ProfileSpecificInitializer(DynamoWeatherSensorRepository dynamoWeatherSensorRepository) {
        this.dynamoWeatherSensorRepository = dynamoWeatherSensorRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Profile("local")
    public void onApplicationReadyForLocal() {
        logger.info("Application started with 'local' profile - ensuring DynamoDB table exists");
        dynamoWeatherSensorRepository.createTable();
    }
}
