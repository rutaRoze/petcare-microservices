# Use a base image with Java
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file
COPY target/service-registry-0.0.1-SNAPSHOT.jar /app/service-registry.jar

# Expose the port that Eureka Server uses
EXPOSE 8761

# Command to run the Eureka Server
ENTRYPOINT ["java", "-jar", "/app/service-registry.jar"]
