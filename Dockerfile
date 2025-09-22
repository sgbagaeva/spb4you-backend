# Стадия сборки
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /app

# Копируем файлы для кэширования зависимостей
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# ДАЕМ ПРАВА НА ВЫПОЛНЕНИЕ GRADLEW (это важно!)
RUN chmod +x gradlew

# Скачиваем зависимости (кэшируется)
RUN ./gradlew dependencies --no-daemon

# Копируем исходный код
COPY src src

# Собираем приложение
RUN ./gradlew build -x test --no-daemon

# Стадия рантайма
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Копируем собранный JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# Создаем пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]