package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.Location;
import com.example.spb4you_backend.models.Photo;
import com.example.spb4you_backend.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // Говорит Spring, что этот класс обрабатывает веб-запросы
public class PhotoController {

    @Autowired
    PhotoService photoService;

    @GetMapping("/photos/list")
    public ResponseEntity<List<Photo>> listLocations() {
        List<Photo> photos = photoService.findAll();
        return ResponseEntity.ok(photos); // Возвращаем список всех фото со всеми полями с кодом 200
    }

    @GetMapping("/photos/{photoId}")
    public ResponseEntity<Photo> getLocationDetails(@PathVariable("photoId") Integer locationId) {
        Photo photo = photoService.findById(locationId).orElse(null);
        assert photo != null;
        return ResponseEntity.ok(photo); // Возвращаем набор полей фото по соответствующему ID с кодом 200
    }

    @CrossOrigin(origins = {"http://localhost:3000", "https://spb4you-frontend-sgbagaeva.amvera.io/"})
    @GetMapping("/photos")
    public Map<String, String> getAllPhotoUrls() {
        List<Photo> photos = photoService.findAll();

        // Преобразуем в формат { "название": "url" }
        Map<String, String> photoUrls = new HashMap<>();
        for (Photo photo : photos) {
            photoUrls.put(photo.getName(), photo.getUrl());
        }

        return photoUrls;
    }
}
