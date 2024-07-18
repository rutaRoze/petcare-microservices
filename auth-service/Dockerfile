# Use an Alpine-based image with Java 21
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file
COPY target/auth-service-0.0.1-SNAPSHOT.jar /app/auth-service.jar

# Expose the port that Auth Service uses
EXPOSE 8084

# Command to run the Auth Service
ENTRYPOINT ["java", "-jar", "/app/auth-service.jar"]
