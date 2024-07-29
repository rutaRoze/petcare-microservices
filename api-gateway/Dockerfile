# Use an Alpine-based image with Java 21
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file
COPY target/api-gateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar

# Expose the port that API Gateway uses
EXPOSE 8080

# Command to run the API Gateway
ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]
