package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.Location;
import com.example.spb4you_backend.models.Photo;
import com.example.spb4you_backend.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController // Говорит Spring, что этот класс обрабатывает веб-запросы
public class PhotoController {

    @Autowired
    PhotoService photoService;

    @GetMapping("/photos/list")
    public ResponseEntity<List<Photo>> listLocations() {
        List<Photo> photos = photoService.findAll();
        return ResponseEntity.ok(photos); // Возвращаем список всех локаций со всеми полями для каждой с кодом 200
    }
}
