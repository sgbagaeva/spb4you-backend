package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.Route;
import com.example.spb4you_backend.services.PointService;
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
}
