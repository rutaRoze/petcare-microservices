# Use an Alpine-based image with Java 21
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file
COPY target/user-service-0.0.1-SNAPSHOT.jar /app/user-service.jar

# Expose the port that User Service uses
EXPOSE 8081

# Command to run the User Service
ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]
