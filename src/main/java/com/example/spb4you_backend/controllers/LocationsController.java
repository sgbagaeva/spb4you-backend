package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.*;
import com.example.spb4you_backend.services.LocationService;
import com.example.spb4you_backend.services.PhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/locations")
public class LocationsController {

    private static final Logger logger = LoggerFactory.getLogger(LocationsController.class);

    @Autowired
    private LocationService locationService;

    @Autowired
    private PhotoService photoService;

    /**
     * GET /locations
     * @return
     */
    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        try {
            List<Location> locations = locationService.getAllLocations();
            return ResponseEntity.ok(locations);
        } catch (Exception e) {
            logger.error("Ошибка при получении списка локаций: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /locations/{id}
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Integer id) {
        try {
            Location location = locationService.getLocationById(id);
            return ResponseEntity.ok(location);
        } catch (RuntimeException e) {
            logger.error("Локация {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при получении локации {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /locations
     * @param location
     * @return
     */
    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        try {
            if (location.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Location created = locationService.createLocation(location);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            logger.error("Ошибка при создании локации: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Ошибка при создании локации: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * PUT /locations/{id} - полное обновление локации
     * @param id
     * @param location
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(
            @PathVariable Integer id,
            @RequestBody Location location) {
        try {
            if (location.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Location updated = locationService.updateLocation(id, location);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            logger.error("Локация {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при обновлении локации {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * PATCH /locations/{id} - частичное обновление локации
     * @param id
     * @param updates
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Location> patchLocation(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> updates) {
        try {
            logger.info("PATCH запрос для локации {} с данными: {}", id, updates);
            Location location = locationService.getLocationById(id);

            // Обновляем только те поля, которые пришли в запросе
            if (updates.containsKey("name")) {
                String name = (String) updates.get("name");
                if (name != null && !name.trim().isEmpty()) {
                    location.setName(name);
                }
            }

            if (updates.containsKey("description")) {
                location.setDescription((String) updates.get("description"));
            }

            if (updates.containsKey("main_photo_id")) {
                Integer mainPhotoId = (Integer) updates.get("main_photo_id");
                location.setMainPhotoId(mainPhotoId);
            }

            if (updates.containsKey("mainPhotoId")) {
                Integer mainPhotoId = (Integer) updates.get("mainPhotoId");
                location.setMainPhotoId(mainPhotoId);
            }

            if (updates.containsKey("photo_ids")) {
                @SuppressWarnings("unchecked")
                List<Integer> photoIds = (List<Integer>) updates.get("photo_ids");
                location.setPhotoIds(photoIds);
            }

            if (updates.containsKey("photoIds")) {
                @SuppressWarnings("unchecked")
                List<Integer> photoIds = (List<Integer>) updates.get("photoIds");
                location.setPhotoIds(photoIds);
            }

            if (updates.containsKey("category_ids")) {
                @SuppressWarnings("unchecked")
                List<Integer> categoryIds = (List<Integer>) updates.get("category_ids");
                location.setCategoryIds(categoryIds);
            }

            if (updates.containsKey("tag_ids")) {
                @SuppressWarnings("unchecked")
                List<Integer> tagIds = (List<Integer>) updates.get("tag_ids");
                location.setTagIds(tagIds);
            }

            if (updates.containsKey("likes")) {
                Integer likes = (Integer) updates.get("likes");
                location.setLikes(likes);
            }

            Location updatedLocation = locationService.updateLocation(id, location);
            return ResponseEntity.ok(updatedLocation);
        } catch (RuntimeException e) {
            logger.error("Локация {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при частичном обновлении локации {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DELETE /locations/{id}
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Integer id) {
        logger.info("=== DELETE ЗАПРОС НА УДАЛЕНИЕ ЛОКАЦИИ ID: {} ===", id);
        try {
            // Проверяем существование перед удалением
            Location location = locationService.getLocationById(id);
            logger.info("Локация найдена: ID={}, NAME={}", location.getId(), location.getName());

            locationService.deleteLocation(id);
            logger.info("Локация {} успешно удалена", id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Ошибка при удалении: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /locations/{id}/main-photo
     * @param id
     * @return
     */
    @GetMapping("/{id}/main-photo")
    public ResponseEntity<Photo> getMainPhoto(@PathVariable Integer id) {
        try {
            Photo mainPhoto = locationService.getMainPhoto(id);
            if (mainPhoto == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(mainPhoto);
        } catch (Exception e) {
            logger.error("Ошибка при получении основной фотографии локации {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /locations/{id}/main-photo
     * @param id
     * @param photo
     * @return
     */
    @PostMapping("/{id}/main-photo")
    public ResponseEntity<Photo> uploadMainLocationPhoto(
            @PathVariable Integer id,
            @RequestParam("photo") MultipartFile photo) {
        try {
            if (photo == null || photo.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            Photo uploadedPhoto = locationService.uploadMainLocationPhoto(id, photo);
            return ResponseEntity.ok(uploadedPhoto);
        } catch (RuntimeException e) {
            logger.error("Локация {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при загрузке главного фото для локации {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /locations/{id}/photos
     * @param id
     * @return
     */
    @GetMapping("/{id}/photos")
    public ResponseEntity<List<Photo>> getLocationPhotos(@PathVariable Integer id) {
        try {
            List<Photo> photos = locationService.getLocationPhotos(id);
            return ResponseEntity.ok(photos);
        } catch (RuntimeException e) {
            logger.error("Локация {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при получении фото локации {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /locations/{id}/photos - загрузка нескольких фотографий
     * @param id
     * @param photos
     * @return
     */
    @PostMapping("/{id}/photos")
    public ResponseEntity<List<Photo>> uploadLocationPhotos(
            @PathVariable Integer id,
            @RequestParam("photos") MultipartFile[] photos) {
        try {
            if (photos == null || photos.length == 0) {
                return ResponseEntity.badRequest().build();
            }
            List<Photo> uploadedPhotos = locationService.uploadLocationPhotos(id, Arrays.asList(photos));
            return ResponseEntity.ok(uploadedPhotos);
        } catch (RuntimeException e) {
            logger.error("Локация {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при загрузке фото для локации {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /locations/{id}/photos/{photoId}
     * @param id
     * @param photoId
     * @return
     */
    @DeleteMapping("/{id}/photos/{photoId}")
    public ResponseEntity<Void> deleteLocationPhoto(
            @PathVariable Integer id,
            @PathVariable Integer photoId) {
        try {
            locationService.deleteLocationPhoto(id, photoId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Локация {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при удалении фото {} локации {}: {}", photoId, id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /locations/{id}/additional-info
     * @param id
     * @return
     */
    @GetMapping("/{id}/additional-info")
    public ResponseEntity<List<AdditionalInfo>> getLocationAdditionalInfo(@PathVariable Integer id) {
        try {
            Location location = locationService.getLocationById(id);
            return ResponseEntity.ok(location.getAdditionalInfo());
        } catch (RuntimeException e) {
            logger.error("Локация {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при получении доп. информации локации {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /locations/{id}/additional-info
     * @param id
     * @param additionalInfo
     * @return
     */
    @PostMapping("/{id}/additional-info")
    public ResponseEntity<AdditionalInfo> addAdditionalInfo(
            @PathVariable Integer id,
            @RequestBody AdditionalInfo additionalInfo) {
        try {
            AdditionalInfo saved = locationService.addAdditionalInfo(id, additionalInfo);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            logger.error("Локация {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при добавлении доп. информации для локации {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /locations/{id}/additional-info/{infoId}
     * @param id
     * @param infoId
     * @param additionalInfo
     * @return
     */
    @PutMapping("/{id}/additional-info/{infoId}")
    public ResponseEntity<AdditionalInfo> updateAdditionalInfo(
            @PathVariable Integer id,
            @PathVariable Integer infoId,
            @RequestBody AdditionalInfo additionalInfo) {
        try {
            AdditionalInfo updated = locationService.updateAdditionalInfo(id, infoId, additionalInfo);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            logger.error("Локация {} или информация {} не найдены: {}", id, infoId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при обновлении доп. информации {} для локации {}: {}", infoId, id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /locations/{id}/additional-info/{infoId}
     * @param id
     * @param infoId
     * @return
     */
    @DeleteMapping("/{id}/additional-info/{infoId}")
    public ResponseEntity<Void> deleteAdditionalInfo(
            @PathVariable Integer id,
            @PathVariable Integer infoId) {
        try {
            locationService.deleteAdditionalInfo(id, infoId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Локация {} или информация {} не найдены: {}", id, infoId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при удалении доп. информации {} для локации {}: {}", infoId, id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /locations/{id}/points
     * @param id
     * @return
     */
    @GetMapping("/{id}/points")
    public ResponseEntity<List<Point>> getLocationPoints(@PathVariable Integer id) {
        try {
            Location location = locationService.getLocationById(id);
            return ResponseEntity.ok(location.getPoints());
        } catch (RuntimeException e) {
            logger.error("Локация {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при получении точек локации {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /locations/{id}/points
     * @param id
     * @param point
     * @return
     */
    @PostMapping("/{id}/points")
    public ResponseEntity<Point> addPointToLocation(
            @PathVariable Integer id,
            @RequestBody Point point) {
        try {
            Point savedPoint = locationService.addPointToLocation(id, point);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPoint);
        } catch (RuntimeException e) {
            logger.error("Локация {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при добавлении точки к локации {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /locations/{id}/points/{pointId}
     * @param id
     * @param pointId
     * @param point
     * @return
     */
    @PutMapping("/{id}/points/{pointId}")
    public ResponseEntity<Point> updatePoint(
            @PathVariable Integer id,
            @PathVariable Integer pointId,
            @RequestBody Point point) {
        try {
            Point updatedPoint = locationService.updatePoint(id, pointId, point);
            return ResponseEntity.ok(updatedPoint);
        } catch (RuntimeException e) {
            logger.error("Локация {} или точка {} не найдены: {}", id, pointId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при обновлении точки {} для локации {}: {}", pointId, id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PATCH /locations/{id}/points/{pointId} - частичное обновление точки
     * @param id
     * @param pointId
     * @param updates
     * @return
     */
    @PatchMapping("/{id}/points/{pointId}")
    public ResponseEntity<Point> patchPoint(
            @PathVariable Integer id,
            @PathVariable Integer pointId,
            @RequestBody Map<String, Object> updates) {
        try {
            logger.info("PATCH запрос для точки {} локации {} с данными: {}", pointId, id, updates);
            Point point = locationService.getPointById(id, pointId);

            if (updates.containsKey("name")) {
                String name = (String) updates.get("name");
                if (name != null && !name.trim().isEmpty()) {
                    point.setName(name);
                }
            }

            if (updates.containsKey("description")) {
                point.setDescription((String) updates.get("description"));
            }

            if (updates.containsKey("latitude")) {
                Double latitude = (Double) updates.get("latitude");
                point.setLatitude(latitude);
            }

            if (updates.containsKey("longitude")) {
                Double longitude = (Double) updates.get("longitude");
                point.setLongitude(longitude);
            }

            Point updatedPoint = locationService.updatePoint(id, pointId, point);
            return ResponseEntity.ok(updatedPoint);
        } catch (RuntimeException e) {
            logger.error("Локация {} или точка {} не найдены: {}", id, pointId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при частичном обновлении точки {} для локации {}: {}", pointId, id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /locations/{id}/points/{pointId}
     * @param id
     * @param pointId
     * @return
     */
    @DeleteMapping("/{id}/points/{pointId}")
    public ResponseEntity<Void> deletePointFromLocation(
            @PathVariable Integer id,
            @PathVariable Integer pointId) {
        try {
            locationService.deletePointFromLocation(id, pointId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Локация {} или точка {} не найдены: {}", id, pointId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при удалении точки {} из локации {}: {}", pointId, id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}