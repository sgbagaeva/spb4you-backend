# Простая одностадийная сборка
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Копируем исходный код
COPY . .

# Собираем приложение
RUN ./gradlew clean build -x test --no-daemon

# Стадия запуска
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Копируем JAR из стадии сборки
COPY --from=builder /app/build/libs/spb4you-backend-0.0.1-SNAPSHOT.jar app.jar

# Создаем пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
