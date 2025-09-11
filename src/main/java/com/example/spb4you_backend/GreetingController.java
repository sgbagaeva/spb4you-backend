package com.example.spb4you_backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Map;

@RestController // Говорит Spring, что этот класс обрабатывает веб-запросы
public class GreetingController {

    // Разрешаем запросы с локального React-сервера и с будущего домена фронтенда
    @CrossOrigin(origins = {"http://localhost:3000", "https://spb4you-frontend-sgbagaeva.amvera.io/"})
    // Разрешаем запросы с любого домена (для теста) или укажите точный URL вашего фронтенда
    // @CrossOrigin(origins = "*")
// Или лучше так (замените на ваш реальный URL фронтенда после деплоя):
// @CrossOrigin(origins = "https://spb4you-frontend.amvera.io")
    @GetMapping("/api/greeting")
    public Map<String, String> getGreeting() {
        // Возвращаем объект, а не строку
        return Map.of("message", "Hello from Spring Boot on Amvera!");
    }
}
