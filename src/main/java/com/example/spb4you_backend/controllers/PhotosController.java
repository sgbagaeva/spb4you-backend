package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.Photo;
import com.example.spb4you_backend.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/photos")
public class PhotosController {

    @Autowired
    PhotoService photoService;

    @GetMapping()
    public ResponseEntity<List<Photo>> listPhotos() {
        return ResponseEntity.ok(photoService.findAll());
    }

    @GetMapping("/{photoId}")
    public ResponseEntity<Photo> getPhotoById(@PathVariable("photoId") Integer photoId) {
        return photoService.findById(photoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
