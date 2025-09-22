# Многостадийная сборка
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew build -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

# Явно указываем адрес и порт для Docker
CMD ["java", "-Dserver.address=0.0.0.0", "-Dserver.port=8080", "-jar", "app.jar"]