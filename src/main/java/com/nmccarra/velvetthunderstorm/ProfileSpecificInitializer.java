package com.nmccarra.velvetthunderstorm;


import com.nmccarra.velvetthunderstorm.repository.dynamodb.DynamoWeatherSensorRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProfileSpecificInitializer {

    private final DynamoWeatherSensorRepository dynamoWeatherSensorRepository;

    public ProfileSpecificInitializer(DynamoWeatherSensorRepository dynamoWeatherSensorRepository) {
        this.dynamoWeatherSensorRepository = dynamoWeatherSensorRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Profile("local")
    public void onApplicationReadyForDev() {
        System.out.println("Application started with local profile");
        dynamoWeatherSensorRepository.createTable();
    }
}
