package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.Photo;
import com.example.spb4you_backend.models.Point;
import com.example.spb4you_backend.models.PointPhoto;
import com.example.spb4you_backend.services.PhotoService;
import com.example.spb4you_backend.services.PointPhotoService;
import com.example.spb4you_backend.services.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/points")
public class PointsController {
    @Autowired
    PointService pointService;

    @Autowired
    PointPhotoService pointPhotoService;

    @Autowired
    PhotoService photoService;

    @GetMapping()
    public ResponseEntity<List<Point>> listPoints() {
        return ResponseEntity.ok(pointService.findAll());
    }

    @GetMapping("/{pointId}")
    public ResponseEntity<Point> getPoint(@PathVariable("pointId") Integer pointId) {
        return pointService.findById(pointId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{pointId}/photos")
    public ResponseEntity<List<Photo>> getPointPhotos(@PathVariable("pointId") Integer pointId) {
        List<PointPhoto> pointPhotos = pointPhotoService.findAllByPointId(pointId);

        if (pointPhotos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Photo> photos = pointPhotos.stream()
                .map(PointPhoto::getPhotoId)
                .flatMap(photoId -> photoService.findAll().stream()
                        .filter(photo -> photo.getId().equals(photoId)))
                        .distinct()
                        .toList();

        return photos.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(photos);
    }

    @GetMapping("/{pointId}/photos/{photoId}")
    public ResponseEntity<Photo> getPointPhotoById(
            @PathVariable("pointId") Integer pointId, @PathVariable("photoId") Integer photoId) {
        boolean isAssociated = pointPhotoService.findAllByPointId(pointId).stream()
                .anyMatch(pp -> pp.getId().equals(photoId));

        if (!isAssociated) {
            return ResponseEntity.notFound().build();
        }

        return photoService.findById(photoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
