# Простая одностадийная сборка
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY build/libs/spb4you-backend-0.0.1-SNAPSHOT.jar app.jar

# Создаем пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]