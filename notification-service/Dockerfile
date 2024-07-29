# Use an Alpine-based image with Java 21
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file
COPY target/notification-service-0.0.1-SNAPSHOT.jar /app/notification-service.jar

# Expose the port that Notification Service uses
EXPOSE 8081

# Command to run the Notification Service
ENTRYPOINT ["java", "-jar", "/app/notification-service.jar"]
