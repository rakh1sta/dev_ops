# Dockerfile for Users Service
# Located at: services/users-service/Dockerfile

# BUILD STAGE
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy the entire project structure to resolve parent POM and dependencies
COPY pom.xml .
COPY settings.gradle .
COPY build.gradle .
COPY api-gateway/ ./api-gateway/
COPY discovery-server/ ./discovery-server/
COPY services/ ./services/

# Build only the users-service module and its dependencies
RUN mvn clean package -pl "services/users-service" -am -DskipTests -B -q

# PACKAGE STAGE
FROM eclipse-temurin:17-jre-alpine

RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

WORKDIR /app

COPY --from=builder /app/services/users-service/target/*.jar app.jar
RUN chown appuser:appuser app.jar

USER appuser

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]