package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.Category;
import com.example.spb4you_backend.models.Location;
import com.example.spb4you_backend.models.Photo;
import com.example.spb4you_backend.models.Route;
import com.example.spb4you_backend.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    private static final Logger logger = LoggerFactory.getLogger(CategoriesController.class);

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private StorageService storageService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        try {
            List<Category> categories = categoryService.findAll();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Ошибка при получении категорий: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Integer id) {
        try {
            return categoryService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Ошибка при получении категории {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam String type) {

        try {
            logger.info("Создание категории: name={}, type={}", name, type);

            // Конвертируем английский тип в русский для БД
            String dbType = convertToDbType(type);

            Category category = categoryService.createCategory(name, description, dbType);
            return ResponseEntity.status(HttpStatus.CREATED).body(category);
        } catch (Exception e) {
            logger.error("Ошибка при создании категории: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Integer id,
            @RequestBody Category categoryData) {

        try {
            logger.info("=== UPDATE CATEGORY ===");
            logger.info("ID: {}", id);
            logger.info("Name: {}", categoryData.getName());
            logger.info("Type from front: {}", categoryData.getType());

            // Конвертируем английский тип в русский для БД
            String dbType = convertToDbType(categoryData.getType());
            logger.info("Type for DB: {}", dbType);

            Category category = categoryService.updateCategory(
                    id,
                    categoryData.getName(),
                    categoryData.getDescription(),
                    dbType
            );

            return ResponseEntity.ok(category);
        } catch (RuntimeException e) {
            logger.error("Category not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating category: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Метод конвертации
    private String convertToDbType(String apiType) {
        if ("location".equalsIgnoreCase(apiType)) {
            return "Локации";
        }
        if ("route".equalsIgnoreCase(apiType)) {
            return "Маршруты";
        }
        return apiType; // fallback
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Категория {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при удалении категории {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/locations")
    public ResponseEntity<List<Category>> getCategoriesOfLocations() {
        try {
            List<Category> categories = categoryService.findAll().stream()
                    .filter(category -> "Локации".equals(category.getType()) || "location".equals(category.getType()))
                    .toList();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Ошибка при получении категорий локаций: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/routes")
    public ResponseEntity<List<Category>> getCategoriesOfRoutes() {
        try {
            List<Category> categories = categoryService.findAll().stream()
                    .filter(category -> "Маршруты".equals(category.getType()) || "route".equals(category.getType()))
                    .toList();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Ошибка при получении категорий маршрутов: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/locations")
    public ResponseEntity<List<Location>> getCategoryLocations(@PathVariable Integer id) {
        try {
            Category category = categoryService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));

            List<Location> locations = locationService.findAll().stream()
                    .filter(location -> location.getCategoryIds() != null && location.getCategoryIds().contains(id))
                    .toList();

            // Заполняем main_photo_url для каждой локации
            for (Location location : locations) {
                if (location.getMainPhotoId() != null) {
                    Optional<Photo> photoOpt = photoService.findById(location.getMainPhotoId());
                    if (photoOpt.isPresent()) {
                        String url = storageService.getFileUrl(photoOpt.get().getFileKey());
                        location.setMainPhotoUrl(url);
                    }
                }
            }

            return ResponseEntity.ok(locations);
        } catch (RuntimeException e) {
            logger.error("Категория {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при получении локаций категории {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/routes")
    public ResponseEntity<List<Route>> getCategoryRoutes(@PathVariable Integer id) {
        try {
            Category category = categoryService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));

            List<Route> routes = routeService.findAll().stream()
                    .filter(route -> route.getCategoryIds() != null && route.getCategoryIds().contains(id))
                    .toList();

            // Заполняем main_photo_url для каждого маршрута
            for (Route route : routes) {
                if (route.getMainPhotoId() != null) {
                    Optional<Photo> photoOpt = photoService.findById(route.getMainPhotoId());
                    if (photoOpt.isPresent()) {
                        String url = storageService.getFileUrl(photoOpt.get().getFileKey());
                        route.setMainPhotoUrl(url);
                    }
                }
            }

            return ResponseEntity.ok(routes);
        } catch (RuntimeException e) {
            logger.error("Категория {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при получении маршрутов категории {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/locations")
    public ResponseEntity<Void> updateCategoryLocations(
            @PathVariable Integer id,
            @RequestBody List<Integer> locationIds) {

        try {
            Category category = categoryService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));

            if (!"Локации".equals(category.getType()) && !"location".equals(category.getType())) {
                return ResponseEntity.badRequest().build();
            }

            // Обновляем category_ids у каждой локации
            List<Location> allLocations = locationService.findAll();

            for (Location location : allLocations) {
                List<Integer> currentCategoryIds = location.getCategoryIds();
                if (currentCategoryIds == null) {
                    currentCategoryIds = new java.util.ArrayList<>();
                }

                boolean shouldHaveCategory = locationIds.contains(location.getId());
                boolean currentlyHasCategory = currentCategoryIds.contains(id);

                if (shouldHaveCategory && !currentlyHasCategory) {
                    // Добавляем ID категории
                    currentCategoryIds.add(id);
                    location.setCategoryIds(currentCategoryIds);
                    locationService.save(location);
                    logger.info("Добавлена категория {} к локации {}", id, location.getId());
                } else if (!shouldHaveCategory && currentlyHasCategory) {
                    // Удаляем ID категории
                    currentCategoryIds.remove(id);
                    location.setCategoryIds(currentCategoryIds);
                    locationService.save(location);
                    logger.info("Удалена категория {} из локации {}", id, location.getId());
                }
            }

            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Категория {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при обновлении связей локаций категории {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/routes")
    public ResponseEntity<Void> updateCategoryRoutes(
            @PathVariable Integer id,
            @RequestBody List<Integer> routeIds) {

        try {
            Category category = categoryService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));

            if (!"Маршруты".equals(category.getType()) && !"route".equals(category.getType())) {
                return ResponseEntity.badRequest().build();
            }

            // Обновляем category_ids у каждого маршрута
            List<Route> allRoutes = routeService.findAll();

            for (Route route : allRoutes) {
                List<Integer> currentCategoryIds = route.getCategoryIds();
                if (currentCategoryIds == null) {
                    currentCategoryIds = new java.util.ArrayList<>();
                }

                boolean shouldHaveCategory = routeIds.contains(route.getId());
                boolean currentlyHasCategory = currentCategoryIds.contains(id);

                if (shouldHaveCategory && !currentlyHasCategory) {
                    // Добавляем ID категории
                    currentCategoryIds.add(id);
                    route.setCategoryIds(currentCategoryIds);
                    routeService.save(route);
                    logger.info("Добавлена категория {} к маршруту {}", id, route.getId());
                } else if (!shouldHaveCategory && currentlyHasCategory) {
                    // Удаляем ID категории
                    currentCategoryIds.remove(id);
                    route.setCategoryIds(currentCategoryIds);
                    routeService.save(route);
                    logger.info("Удалена категория {} из маршрута {}", id, route.getId());
                }
            }

            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Категория {} не найдена: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при обновлении связей маршрутов категории {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}