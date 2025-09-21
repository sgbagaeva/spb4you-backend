package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.Location;
import com.example.spb4you_backend.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/locations")
public class LocationsController {

    @Autowired
    LocationService locationService;

    @GetMapping("/info/list")
    public ResponseEntity<List<Location>> listLocations() {
        List<Location> locations = locationService.findAll();
        return ResponseEntity.ok(locations); // Возвращаем список всех локаций со всеми полями для каждой с кодом 200
    }

    @GetMapping("/info/{locationId}")
    public ResponseEntity<Location> getLocationDetails(@PathVariable("locationId") Integer locationId) {
        Location location = locationService.findById(locationId).orElse(null);
        assert location != null;
        return ResponseEntity.ok(location); // Возвращаем набор полей локации по соответствующему ID с кодом 200
    }
}
