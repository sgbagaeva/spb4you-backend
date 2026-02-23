package com.example.spb4you_backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // разрешаем все эндпоинты
                .allowedOrigins("https://spb4you-frontend-sgbagaeva.amvera.io") // адрес React-фронтенда
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // разрешенные HTTP методы
                .allowedHeaders("*") // разрешаем все заголовки
                .allowCredentials(true) // разрешаем куки/сессии
                .maxAge(3600); // время кеширования preflight запроса
    }
}