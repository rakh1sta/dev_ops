# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# 1. Root pom.xml ni nusxalash
COPY pom.xml .

# 2. BARCHA modullarning pom.xml fayllarini nusxalash
# Maven loyiha xaritasini (modules) tekshirayotganda bular diskda bo'lishi shart
COPY api-gateway/pom.xml api-gateway/
COPY discovery-server/pom.xml discovery-server/
COPY services/users-service/pom.xml services/users-service/
COPY services/product-service/pom.xml services/product-service/
COPY services/order-service/pom.xml services/order-service/
COPY services/notification-service/pom.xml services/notification-service/
COPY services/statistics-service/pom.xml services/statistics-service/

# 3. Dependency-larni keshga olish
RUN mvn dependency:go-offline -B -q

# 4. Faqat statistics-service kodini nusxalash
COPY services/statistics-service/src ./services/statistics-service/src

# 5. Build qilish (-am flagi bilan barcha bog'liqliklarni inobatga oladi)
RUN mvn clean package -pl services/statistics-service -am -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Zaruriy utilitalar (curl health check uchun kerak)
RUN apk add --no-cache curl

# JAR faylni builder bosqichidan nusxalash
COPY --from=builder /app/services/statistics-service/target/statistics-service-*.jar app.jar

# Statistics service porti
EXPOSE 8085

# Health check (Spring Boot Actuator orqali)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8085/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]