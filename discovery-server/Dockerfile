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

# 4. Faqat discovery-server kodini nusxalash
COPY discovery-server/src ./discovery-server/src

# 5. Build qilish (-am flagi bilan)
RUN mvn clean package -pl discovery-server -am -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Zaruriy utilitalar
RUN apk add --no-cache curl

# JAR faylni nusxalash
COPY --from=builder /app/discovery-server/target/discovery-server-*.jar app.jar

# Eureka porti
EXPOSE 8761

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8761/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]