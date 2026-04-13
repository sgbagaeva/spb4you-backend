package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.*;
import com.example.spb4you_backend.models.links.RouteAdditionalInfo;
import com.example.spb4you_backend.models.links.RoutePoint;
import com.example.spb4you_backend.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/routes")
public class RoutesController {

    @Autowired
    private RouteService routeService;

    @Autowired
    private PointService pointService;

    @Autowired
    private RoutePointService routePointService;

    @Autowired
    private RouteAdditionalInfoService routeAdditInfoService;

    @Autowired
    private AdditionalInfoService additionalInfoService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @GetMapping()
    public ResponseEntity<List<Route>> listRoutes() {
        return ResponseEntity.ok(routeService.findAll());
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<Route> getRoute(@PathVariable("routeId") Integer routeId) {
        return routeService.findById(routeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{routeId}/points")
    public ResponseEntity<List<Point>> getRoutePoints(@PathVariable("routeId") Integer routeId) {
        List<RoutePoint> routePoints = routePointService.findAllByRouteId(routeId);

        if (routePoints.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Point> points = routePoints.stream()
                .map(RoutePoint::getPointId)
                .flatMap(pointId -> pointService.findAll().stream()
                        .filter(point -> point.getId().equals(pointId)))
                .distinct()
                .toList();

        return points.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(points);
    }

    @GetMapping("/{routeId}/points/{pointId}")
    public ResponseEntity<Point> getRoutePointDetails(@PathVariable("routeId") Integer routeId,
                                                         @PathVariable("pointId") Integer pointId) {
        boolean isAssociated = routePointService.findAllByRouteId(routeId).stream()
                .anyMatch(lp -> lp.getPointId().equals(pointId));

        if (!isAssociated) {
            return ResponseEntity.notFound().build();
        }

        // Проверяем, существует ли сама точка
        return pointService.findById(pointId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{routeId}/addInfoList")
    public ResponseEntity<List<AdditionalInfo>> getRouteAddInfoList(
            @PathVariable("routeId") Integer routeId) {

        List<RouteAdditionalInfo> routeAdditInfoList = routeAdditInfoService.findAllByRouteId(routeId);

        if (routeAdditInfoList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<AdditionalInfo> additionalInfoList = routeAdditInfoList.stream()
                .map(RouteAdditionalInfo::getAdditionalInfoId)
                .flatMap(additInfoId -> additionalInfoService.findAll().stream()
                        .filter(additInfo -> additInfo.getId().equals(additInfoId)))
                .distinct()
                .toList();

        return additionalInfoList.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(additionalInfoList);
    }

    @GetMapping("/{routeId}/addInfoList/{addInfoId}")
    public ResponseEntity<AdditionalInfo> getRouteAddInfoById(
            @PathVariable("routeId") Integer routeId, @PathVariable("addInfoId") Integer addInfoId) {
        boolean isAssociated = routeAdditInfoService.findAllByRouteId(routeId).stream()
                .anyMatch(lai -> lai.getId().equals(addInfoId));

        if (!isAssociated) {
            return ResponseEntity.notFound().build();
        }

        return additionalInfoService.findById(addInfoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{routeId}/categories")
    public ResponseEntity<List<Category>> getRouteCategories(@PathVariable("routeId") Integer routeId) {

        Optional<Route> routeOpt = routeService.findById(routeId);
        if (routeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Route route = routeOpt.get();

        List<Category> categories = categoryService.findAll()
                .stream()
                .filter(category -> route.getCategoryIds().contains(category.getId()))
                .toList();

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{routeId}/tags")
    public ResponseEntity<List<Tag>> getRouteTags(@PathVariable("routeId") Integer routed) {

        Optional<Route> routeOpt = routeService.findById(routed);
        if (routeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Route route = routeOpt.get();

        List<Tag> tags = tagService.findAll()
                .stream()
                .filter(tag -> route.getTagIds().contains(tag.getId()))
                .toList();

        return ResponseEntity.ok(tags);
    }
}
