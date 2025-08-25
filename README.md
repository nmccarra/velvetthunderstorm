Velvet Thunderstorm
==================

## Description
This service manages the storage of weather measurements and provides APIs to retrieve metrics on this data.

## How to Develop

### Build
`./gradlew clean build --no-daemon`

### Tests
`./gradlew clean test`

### Run application

Running docker-compose will start the application along with a local DynamoDb database.

#### Requirements

* Docker

```shell
  docker-compose up --build
```

### Documentation
The API documentation is available via [Swagger UI](http://localhost:8080/swagger-ui.html) after starting the application.

