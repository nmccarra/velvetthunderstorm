FROM gradle:8.4.0-jdk21-alpine AS build
WORKDIR /app
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle/libs.versions.toml ./gradle/
COPY src ./src
#RUN gradle clean build -x test --no-daemon
## Adjust the path to where your JAR actually is
#COPY build/libs/*.jar app.jar
## OR if using a different build system:
## COPY path/to/your/app.jar app.jar
#
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]

# Build the application
RUN gradle clean build -x test --no-daemon

# Runtime stage
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
