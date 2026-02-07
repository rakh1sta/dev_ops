# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# 1. Root pom.xml ni nusxalash
COPY pom.xml .

# 2. BARCHA modullarning pom.xml fayllarini nusxalash (BU ASOSIY TUZATISH)
# Maven loyiha strukturasini validatsiya qilishi uchun bular shart
COPY api-gateway/pom.xml api-gateway/
COPY discovery-server/pom.xml discovery-server/
COPY services/users-service/pom.xml services/users-service/
COPY services/product-service/pom.xml services/product-service/
COPY services/order-service/pom.xml services/order-service/
COPY services/notification-service/pom.xml services/notification-service/
COPY services/statistics-service/pom.xml services/statistics-service/

# 3. Dependency-larni keshga olish
RUN mvn dependency:go-offline -B -q

# 4. Faqat api-gateway kodini nusxalash
COPY api-gateway/src ./api-gateway/src

# 5. Build qilish (-am flagi parentni ham inobatga olish uchun)
RUN mvn clean package -pl api-gateway -am -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=builder /app/api-gateway/target/api-gateway-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]