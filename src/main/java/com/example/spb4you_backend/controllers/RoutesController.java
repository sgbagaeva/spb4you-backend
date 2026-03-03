package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.*;
import com.example.spb4you_backend.services.PointService;
import com.example.spb4you_backend.services.RouteAddInfoService;
import com.example.spb4you_backend.services.RoutePointService;
import com.example.spb4you_backend.services.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/routes")
public class RoutesController {

    @Autowired
    RouteService routeService;

    @Autowired
    PointService pointService;

    @Autowired
    RoutePointService routePointService;

    @Autowired
    RouteAddInfoService routeAddInfoService;

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
    public ResponseEntity<List<RouteAddInfo>> getRouteAddInfoList(
            @PathVariable("routeId") Integer routeId) {
        return ResponseEntity.ok(routeAddInfoService.findAllByRouteId(routeId));
    }

    @GetMapping("/{routeId}/addInfoList/{addInfoId}")
    public ResponseEntity<RouteAddInfo> getRouteAddInfoById(
            @PathVariable("routeId") Integer routeId, @PathVariable("addInfoId") Integer addInfoId) {
        boolean isAssociated = routeAddInfoService.findAllByRouteId(routeId).stream()
                .anyMatch(lai -> lai.getId().equals(addInfoId));

        if (!isAssociated) {
            return ResponseEntity.notFound().build();
        }

        return routeAddInfoService.findById(addInfoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
