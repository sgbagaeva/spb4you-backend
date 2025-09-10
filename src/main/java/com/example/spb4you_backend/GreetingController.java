package com.example.spb4you_backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController // Говорит Spring, что этот класс обрабатывает веб-запросы
public class GreetingController {

//    // Разрешаем запросы с localhost:3000 (адрес React-приложения)
//    @CrossOrigin(origins = "http://localhost:3000")
//    @GetMapping("/api/greeting") // Обрабатывает GET запросы на /api/greeting
//    public String getGreeting() {
//        return "{\"message\": \"Hello from Spring Boot!\"}"; // Возвращаем простой JSON
//    }
    // Разрешаем запросы с любого домена (для теста) или укажите точный URL вашего фронтенда
    @CrossOrigin(origins = "*")
// Или лучше так (замените на ваш реальный URL фронтенда после деплоя):
// @CrossOrigin(origins = "https://spb4you-frontend.amvera.io")
    @GetMapping("/api/greeting")
    public String getGreeting() {
        return "{\"message\": \"Hello from Spring Boot on Amvera!\"}";
    }
}
