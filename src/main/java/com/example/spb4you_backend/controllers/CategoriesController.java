package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.Category;
import com.example.spb4you_backend.models.Location;
import com.example.spb4you_backend.models.Route;
import com.example.spb4you_backend.services.CategoryService;
import com.example.spb4you_backend.services.LocationService;
import com.example.spb4you_backend.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoriesController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private RouteService routeService;

    @GetMapping()
    public ResponseEntity<List<Category>> listCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Category> getCategory(@PathVariable("categoryId") Integer categoryId) {
        return categoryService.findById(categoryId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/locations")
    public ResponseEntity<List<Category>> getLocationsCategories() {
        List<Category> locationsCategories = categoryService.findAll().stream()
                .filter(category -> "Локации".equals(category.getType()))
                .toList();
        return locationsCategories.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(locationsCategories);
    }

    @GetMapping("/routes")
    public ResponseEntity<List<Category>> getRoutesCategories() {
        List<Category> locationsCategories = categoryService.findAll().stream()
                .filter(category -> "Маршруты".equals(category.getType()))
                .toList();
        return locationsCategories.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(locationsCategories);
    }

    @GetMapping("/{categoryId}/locations")
    public ResponseEntity<List<Location>> getLocationsByCategory(@PathVariable("categoryId") Integer categoryId) {
        return categoryService.findById(categoryId)
                .filter(category -> "Локации".equals(category.getType()))
                .map(category -> {
                    List<Location> locations = locationService.findAll().stream()
                            .filter(location -> location.getCategories().contains(categoryId))
                            .toList();
                    return ResponseEntity.ok(locations);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{categoryId}/routes")
    public ResponseEntity<List<Route>> getRoutesByCategory(@PathVariable("categoryId") Integer categoryId) {
        return categoryService.findById(categoryId)
                .filter(category -> "Маршруты".equals(category.getType()))
                .map(category -> {
                    List<Route> routes = routeService.findAll().stream()
                            .filter(route -> route.getCategories().contains(categoryId))
                            .toList();
                    return ResponseEntity.ok(routes);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
