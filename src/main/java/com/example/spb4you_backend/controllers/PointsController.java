package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.Photo;
import com.example.spb4you_backend.models.Point;
import com.example.spb4you_backend.models.links.PointPhoto;
import com.example.spb4you_backend.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/points")
public class PointsController {

    private static final Logger logger = LoggerFactory.getLogger(PointsController.class);

    @Autowired
    private PointService pointService;

    @Autowired
    private PointPhotoService pointPhotoService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private StorageService storageService;

    @GetMapping
    public ResponseEntity<List<Point>> listPoints() {
        try {
            List<Point> points = pointService.findAll();
            return ResponseEntity.ok(points);
        } catch (Exception e) {
            logger.error("Ошибка при получении списка точек: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{pointId}")
    public ResponseEntity<Point> getPoint(@PathVariable("pointId") Integer pointId) {
        try {
            Optional<Point> pointOpt = pointService.findById(pointId);
            return pointOpt.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Ошибка при получении точки {}: {}", pointId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{pointId}/photos")
    public ResponseEntity<List<Photo>> getPointPhotos(@PathVariable("pointId") Integer pointId) {
        try {
            List<PointPhoto> pointPhotos = pointPhotoService.findAllByPointId(pointId);

            if (pointPhotos.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            List<Photo> photos = new ArrayList<>();
            for (PointPhoto pp : pointPhotos) {
                Optional<Photo> photoOpt = photoService.findById(pp.getPhotoId());
                photoOpt.ifPresent(photo -> {
                    photo.setUrl(storageService.getFileUrl(photo.getFileKey()));
                    photos.add(photo);
                });
            }

            return ResponseEntity.ok(photos);
        } catch (Exception e) {
            logger.error("Ошибка при получении фото точки {}: {}", pointId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{pointId}/photos")
    public ResponseEntity<List<Photo>> uploadPointPhotos(
            @PathVariable Integer pointId,
            @RequestParam("photos") List<MultipartFile> files) {

        try {
            // Проверяем, существует ли точка
            Optional<Point> pointOpt = pointService.findById(pointId);
            if (pointOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Получаем текущие связи фото с точкой
            List<PointPhoto> existingPointPhotos = pointPhotoService.findAllByPointId(pointId);
            int sortOrder = existingPointPhotos.stream()
                    .mapToInt(PointPhoto::getSortOrder)
                    .max()
                    .orElse(0) + 1;

            // Загружаем фото в облако
            List<Photo> uploadedPhotos = storageService.savePointPhotos(pointId, files);
            List<Photo> savedPhotos = new ArrayList<>();

            // Сохраняем информацию о каждом фото в БД и добавляем связь с точкой
            for (Photo photo : uploadedPhotos) {
                Photo savedPhoto = photoService.save(photo);
                savedPhoto.setUrl(storageService.getFileUrl(savedPhoto.getFileKey()));
                savedPhotos.add(savedPhoto);

                PointPhoto pointPhoto = new PointPhoto(pointId, savedPhoto.getId(), sortOrder);
                pointPhotoService.save(pointPhoto);
                sortOrder++;
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedPhotos);

        } catch (Exception e) {
            logger.error("Ошибка при загрузке фото точки {}: {}", pointId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{pointId}/photos/{photoId}")
    public ResponseEntity<Photo> getPointPhotoById(
            @PathVariable("pointId") Integer pointId,
            @PathVariable("photoId") Integer photoId) {
        try {
            boolean isAssociated = pointPhotoService.findAllByPointId(pointId).stream()
                    .anyMatch(pp -> pp.getPhotoId().equals(photoId));

            if (!isAssociated) {
                return ResponseEntity.notFound().build();
            }

            Optional<Photo> photoOpt = photoService.findById(photoId);
            if (photoOpt.isPresent()) {
                Photo photo = photoOpt.get();
                photo.setUrl(storageService.getFileUrl(photo.getFileKey()));
                return ResponseEntity.ok(photo);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении фото {} точки {}: {}", photoId, pointId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{pointId}/photos/{photoId}")
    public ResponseEntity<Void> deletePointPhoto(
            @PathVariable Integer pointId,
            @PathVariable Integer photoId) {
        try {
            // Проверяем, существует ли связь
            Optional<PointPhoto> pointPhotoOpt = pointPhotoService.findByPointIdAndPhotoId(pointId, photoId);
            if (pointPhotoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Удаляем связь
            pointPhotoService.deleteByPhotoId(photoId);

            // Удаляем фото из облака и БД
            Optional<Photo> photoOpt = photoService.findById(photoId);
            if (photoOpt.isPresent()) {
                Photo photo = photoOpt.get();
                storageService.deleteFile(photo.getFileKey());  // Удаляем из облака
                photoService.deleteById(photoId);               // Удаляем из БД
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Ошибка при удалении фото {} точки {}: {}", photoId, pointId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}