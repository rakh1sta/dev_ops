# Multi-stage Dockerfile for Statistics Service
# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy the parent pom.xml and the statistics-service pom.xml
COPY pom.xml .
COPY services/statistics-service/pom.xml ./services/statistics-service/pom.xml

# Download dependencies
RUN mvn dependency:go-offline -q

# Copy source code
COPY services/statistics-service/src ./services/statistics-service/src

# Build the application
RUN mvn clean package -pl services/statistics-service -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="Agricultural Digital Platform"

# Install necessary utilities
RUN apk add --no-cache curl

WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/services/statistics-service/target/statistics-service-*.jar app.jar

# Expose port
EXPOSE 8085

# Health check using Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8085/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]