# Use an Alpine-based image with Java 21
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file
COPY target/auditing-service-0.0.1-SNAPSHOT.jar /app/auditing-service.jar

# Expose the port that Auditing Service uses
EXPOSE 8085

# Command to run the Auditing Service
ENTRYPOINT ["java", "-jar", "/app/auditing-service.jar"]
