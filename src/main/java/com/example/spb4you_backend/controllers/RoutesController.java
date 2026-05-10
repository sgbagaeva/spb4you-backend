package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.*;
import com.example.spb4you_backend.services.*;
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
@RequestMapping("/routes")
public class RoutesController {

    private static final Logger logger = LoggerFactory.getLogger(RoutesController.class);

    @Autowired
    private RouteService routeService;

    @Autowired
    private PhotoService photoService;

    /**
     * GET /routes
     * @return
     */
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        try {
            List<Route> routes = routeService.getAllRoutes();
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            logger.error("Ошибка при получении списка маршрутов: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /routes/{id}
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Integer id) {
        try {
            Route route = routeService.getRouteById(id);
            return ResponseEntity.ok(route);
        } catch (RuntimeException e) {
            logger.error("Маршрут {} не найден: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при получении маршрута {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /routes
     * @param route
     * @return
     */
    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        try {
            if (route.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Route created = routeService.createRoute(route);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            logger.error("Ошибка при создании маршрута: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Ошибка при создании маршрута: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * PUT /routes/{id} - полное обновление маршрута
     * @param id
     * @param route
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(
            @PathVariable Integer id,
            @RequestBody Route route) {
        try {
            if (route.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            Route updated = routeService.updateRoute(id, route);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            logger.error("Маршрут {} не найден: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при обновлении маршрута {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * PATCH /routes/{id} - частичное обновление маршрута
     * @param id
     * @param updates
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Route> patchRoute(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> updates) {
        try {
            logger.info("PATCH запрос для маршрута {} с данными: {}", id, updates);
            Route route = routeService.getRouteById(id);

            // Обновляем только те поля, которые пришли в запросе
            if (updates.containsKey("name")) {
                String name = (String) updates.get("name");
                if (name != null && !name.trim().isEmpty()) {
                    route.setName(name);
                }
            }

            if (updates.containsKey("description")) {
                route.setDescription((String) updates.get("description"));
            }

            if (updates.containsKey("main_photo_id")) {
                Integer mainPhotoId = (Integer) updates.get("main_photo_id");
                route.setMainPhotoId(mainPhotoId);
            }

            if (updates.containsKey("mainPhotoId")) {
                Integer mainPhotoId = (Integer) updates.get("mainPhotoId");
                route.setMainPhotoId(mainPhotoId);
            }

            if (updates.containsKey("category_ids")) {
                @SuppressWarnings("unchecked")
                List<Integer> categoryIds = (List<Integer>) updates.get("category_ids");
                route.setCategoryIds(categoryIds);
            }

            if (updates.containsKey("tag_ids")) {
                @SuppressWarnings("unchecked")
                List<Integer> tagIds = (List<Integer>) updates.get("tag_ids");
                route.setTagIds(tagIds);
            }

            if (updates.containsKey("likes")) {
                Integer likes = (Integer) updates.get("likes");
                route.setLikes(likes);
            }

            Route updatedRoute = routeService.updateRoute(id, route);
            return ResponseEntity.ok(updatedRoute);
        } catch (RuntimeException e) {
            logger.error("Маршрут {} не найден: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при частичном обновлении маршрута {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DELETE /routes/{id}
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Integer id) {
        logger.info("DELETE ЗАПРОС НА УДАЛЕНИЕ ЛОКАЦИИ ID: {}", id);
        try {
            // Проверяем существование перед удалением
            Route route = routeService.getRouteById(id);
            logger.info("Маршрут найден: ID={}, NAME={}", route.getId(), route.getName());

            routeService.deleteRoute(id);
            logger.info("Маршрут {} успешно удален", id);
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
     * GET /routes/{id}/main-photo
     * @param id
     * @return
     */
    @GetMapping("/{id}/main-photo")
    public ResponseEntity<Photo> getMainPhoto(@PathVariable Integer id) {
        try {
            Photo mainPhoto = routeService.getMainPhoto(id);
            if (mainPhoto == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(mainPhoto);
        } catch (Exception e) {
            logger.error("Ошибка при получении основной фотографии маршрута {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /routes/{id}/main-photo
     * @param id
     * @param photo
     * @return
     */
    @PostMapping("/{id}/main-photo")
    public ResponseEntity<Photo> uploadMainRoutePhoto(
            @PathVariable Integer id,
            @RequestParam("photo") MultipartFile photo) {
        try {
            if (photo == null || photo.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            Photo uploadedPhoto = routeService.uploadMainLocationPhoto(id, photo);
            return ResponseEntity.ok(uploadedPhoto);
        } catch (RuntimeException e) {
            logger.error("Маршрут {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при загрузке главного фото для маршрута {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /routes/{id}/additional-info
     * @param id
     * @return
     */
    @GetMapping("/{id}/additional-info")
    public ResponseEntity<List<AdditionalInfo>> getRouteAdditionalInfo(@PathVariable Integer id) {
        try {
            Route route = routeService.getRouteById(id);
            return ResponseEntity.ok(route.getAdditionalInfo());
        } catch (RuntimeException e) {
            logger.error("Маршрут {} не найден: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при получении доп. информации маршрута {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /routes/{id}/additional-info
     * @param id
     * @param additionalInfo
     * @return
     */
    @PostMapping("/{id}/additional-info")
    public ResponseEntity<AdditionalInfo> addAdditionalInfo(
            @PathVariable Integer id,
            @RequestBody AdditionalInfo additionalInfo) {
        try {
            AdditionalInfo saved = routeService.addAdditionalInfo(id, additionalInfo);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            logger.error("Маршрут {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при добавлении доп. информации для маршрута {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /routes/{id}/additional-info/{infoId}
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
            AdditionalInfo updated = routeService.updateAdditionalInfo(id, infoId, additionalInfo);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            logger.error("Маршрут {} или информация {} не найдены: {}", id, infoId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при обновлении доп. информации {} для маршрута {}: {}", infoId, id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /routes/{id}/additional-info/{infoId}
     * @param id
     * @param infoId
     * @return
     */
    @DeleteMapping("/{id}/additional-info/{infoId}")
    public ResponseEntity<Void> deleteAdditionalInfo(
            @PathVariable Integer id,
            @PathVariable Integer infoId) {
        try {
            routeService.deleteAdditionalInfo(id, infoId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Маршрут {} или информация {} не найдены: {}", id, infoId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при удалении доп. информации {} для маршрута {}: {}", infoId, id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /routes/{id}/points
     * @param id
     * @return
     */
    @GetMapping("/{id}/points")
    public ResponseEntity<List<Point>> getRoutePoints(@PathVariable Integer id) {
        try {
            Route route = routeService.getRouteById(id);
            return ResponseEntity.ok(route.getPoints());
        } catch (RuntimeException e) {
            logger.error("Маршрут {} не найден: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при получении точек маршрута {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /routes/{id}/points
     * @param id
     * @param point
     * @return
     */
    @PostMapping("/{id}/points")
    public ResponseEntity<Point> addPointToRoute(
            @PathVariable Integer id,
            @RequestBody Point point) {
        try {
            Point savedPoint = routeService.addPointToRoute(id, point);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPoint);
        } catch (RuntimeException e) {
            logger.error("Маршрут {} не найден: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при добавлении точки к маршруту {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /routes/{id}/points/{pointId}
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
            Point updatedPoint = routeService.updatePoint(id, pointId, point);
            return ResponseEntity.ok(updatedPoint);
        } catch (RuntimeException e) {
            logger.error("Маршрут {} или точка {} не найдены: {}", id, pointId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при обновлении точки {} для маршрута {}: {}", pointId, id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PATCH /routes/{id}/points/{pointId} - частичное обновление точки
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
            logger.info("PATCH запрос для точки {} маршрута {} с данными: {}", pointId, id, updates);
            Point point = routeService.getPointById(id, pointId);

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

            Point updatedPoint = routeService.updatePoint(id, pointId, point);
            return ResponseEntity.ok(updatedPoint);
        } catch (RuntimeException e) {
            logger.error("Маршрут {} или точка {} не найдены: {}", id, pointId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при частичном обновлении точки {} для маршрута {}: {}", pointId, id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /routes/{id}/points/{pointId}
     * @param id
     * @param pointId
     * @return
     */
    @DeleteMapping("/{id}/points/{pointId}")
    public ResponseEntity<Void> deletePointFromLocation(
            @PathVariable Integer id,
            @PathVariable Integer pointId) {
        try {
            routeService.deletePointFromRoute(id, pointId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Маршрут {} или точка {} не найдены: {}", id, pointId, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при удалении точки {} из маршрута {}: {}", pointId, id, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}