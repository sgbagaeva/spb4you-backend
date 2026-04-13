package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.*;
import com.example.spb4you_backend.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tags")
public class TagsController {

    private static final Logger logger = LoggerFactory.getLogger(CategoriesController.class);

    @Autowired
    private TagService tagService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private RouteService routeService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private StorageService storageService;


    @GetMapping
    public ResponseEntity<List<Tag>> listTags() {
        try {
            List<Tag> tags = tagService.findAll();
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTag(@PathVariable("id") Integer id) {
        try {
            return tagService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Tag> createTag(
            @RequestParam String name,
            @RequestParam String color) {
        try {
            Tag tag = tagService.createTag(name, color);
            return ResponseEntity.status(HttpStatus.CREATED).body(tag);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tag> updateTag(
            @PathVariable("id") Integer id,
            @RequestParam String name,
            @RequestParam String color) {
        try {
            Tag tag = tagService.updateTag(id, name, color);
            return ResponseEntity.ok(tag);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Integer id) {
        try {
            tagService.deleteTag(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/locations")
    public ResponseEntity<List<Location>> getLocationsByTag(@PathVariable Integer id) {
        try {
            List<Location> locations = locationService.findAll().stream()
                    .filter(location -> location.getTagIds() != null && location.getTagIds().contains(id))
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
        } catch (Exception e) {
            logger.error("Ошибка при получении локаций по тегу {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/routes")
    public ResponseEntity<List<Route>> getRoutesByTag(@PathVariable Integer id) {
        try {
            List<Route> routes = routeService.findAll().stream()
                    .filter(route -> route.getTagIds() != null && route.getTagIds().contains(id))
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
        } catch (Exception e) {
            logger.error("Ошибка при получении маршрутов по тегу {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/locations")
    public ResponseEntity<Void> updateTagLocations(
            @PathVariable Integer id,
            @RequestBody List<Integer> locationIds) {
        try {
            if (!tagService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            tagService.updateLocationRelationships(id, locationIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/routes")
    public ResponseEntity<Void> updateTagRoutes(
            @PathVariable Integer id,
            @RequestBody List<Integer> routeIds) {
        try {
            if (!tagService.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            tagService.updateRouteRelationships(id, routeIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}