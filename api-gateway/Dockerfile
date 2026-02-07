# Dockerfile for API Gateway
# Located at: api-gateway/Dockerfile

# BUILD STAGE
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy the parent pom.xml first for layer caching
COPY pom.xml .

# Install parent POM to local repository
RUN mvn install -N

# Copy the rest of the project
COPY . .

# Build only the api-gateway module and its dependencies
RUN mvn clean package -pl "api-gateway" -am -DskipTests -B -q

# PACKAGE STAGE
FROM eclipse-temurin:17-jre-alpine

RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

WORKDIR /app

COPY --from=builder /app/api-gateway/target/*.jar app.jar
RUN chown appuser:appuser app.jar

USER appuser

EXPOSE 8765

ENTRYPOINT ["java", "-jar", "app.jar"]