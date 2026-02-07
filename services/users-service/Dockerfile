# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# 1. Root pom.xml ni nusxalash
COPY pom.xml .

# 2. BARCHA modullarning pom.xml fayllarini nusxalash
# Bu Maven'ga 'Child module does not exist' xatosini bermaslikka yordam beradi
COPY api-gateway/pom.xml api-gateway/
COPY discovery-server/pom.xml discovery-server/
COPY services/users-service/pom.xml services/users-service/
COPY services/product-service/pom.xml services/product-service/
COPY services/order-service/pom.xml services/order-service/
COPY services/notification-service/pom.xml services/notification-service/
COPY services/statistics-service/pom.xml services/statistics-service/

# 3. Dependency-larni keshga olish
RUN mvn dependency:go-offline -B -q

# 4. Faqat users-service kodini nusxalash
COPY services/users-service/src ./services/users-service/src

# 5. Build qilish (-am flagi parent ierarxiyasini to'g'ri bog'laydi)
RUN mvn clean package -pl services/users-service -am -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Zaruriy utilitalar
RUN apk add --no-cache curl

# JAR faylni builder bosqichidan nusxalash
COPY --from=builder /app/services/users-service/target/users-service-*.jar app.jar

# Users service porti
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]