# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .

# BARCHA modullar pom.xml (Maven xato bermasligi uchun shart)
COPY api-gateway/pom.xml api-gateway/
COPY discovery-server/pom.xml discovery-server/
COPY services/users-service/pom.xml services/users-service/
COPY services/product-service/pom.xml services/product-service/
COPY services/order-service/pom.xml services/order-service/
COPY services/notification-service/pom.xml services/notification-service/
COPY services/statistics-service/pom.xml services/statistics-service/

RUN mvn dependency:go-offline -B -q

# Faqat notification-service kodini nusxalash
COPY services/notification-service/src ./services/notification-service/src

# Build
RUN mvn clean package -pl services/notification-service -am -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=builder /app/services/notification-service/target/notification-service-*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]