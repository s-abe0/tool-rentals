# Build the application
FROM maven:3.9-amazoncorretto-17-alpine AS build

COPY src /app/src
COPY pom.xml /app
WORKDIR /app

RUN mvn clean package "-Dmaven.test.skip=true"

# Copy the jar from the build container and run it
FROM openjdk:17-jdk-alpine

COPY --from=build /app/target/tool-rentals-service.jar /app/
WORKDIR /app

ENTRYPOINT ["java","-jar","tool-rentals-service.jar"]
