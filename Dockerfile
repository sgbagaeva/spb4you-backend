# Стадия сборки
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /app

# Копируем файлы для кэширования зависимостей
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# Даем права на выполнение gradlew
# RUN chmod +x gradlew

# Скачиваем зависимости (кэшируется)
RUN ./gradlew dependencies --no-daemon

# Копируем исходный код
COPY src src

# Собираем приложение
RUN ./gradlew build -x test --no-daemon

# Стадия рантайма
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Устанавливаем wget для healthcheck (легче чем curl)
RUN apk add --no-cache wget

# Копируем собранный JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# Создаем пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

# Используем CMD вместо ENTRYPOINT для лучшей обработки сигналов
CMD ["java", "-jar", "app.jar"]