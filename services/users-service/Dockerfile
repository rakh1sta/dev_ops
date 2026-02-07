# Multi-stage Dockerfile for Users Service
# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy the parent pom.xml and the users-service pom.xml
COPY pom.xml .
COPY services/users-service/pom.xml ./services/users-service/pom.xml

# Download dependencies
RUN mvn dependency:go-offline -q

# Copy source code
COPY services/users-service/src ./services/users-service/src

# Build the application
RUN mvn clean package -pl services/users-service -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="Agricultural Digital Platform"

# Install necessary utilities
RUN apk add --no-cache curl

WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/services/users-service/target/users-service-*.jar app.jar

# Expose port
EXPOSE 8081

# Health check using Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]