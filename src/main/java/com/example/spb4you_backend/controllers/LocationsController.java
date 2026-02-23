package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.Location;
import com.example.spb4you_backend.models.LocationAddInfo;
import com.example.spb4you_backend.models.LocationPoint;
import com.example.spb4you_backend.models.Point;
import com.example.spb4you_backend.services.LocationAddInfoService;
import com.example.spb4you_backend.services.LocationPointService;
import com.example.spb4you_backend.services.LocationService;
import com.example.spb4you_backend.services.PointService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/locations")
public class LocationsController {

    @Autowired
    LocationService locationService;

    @Autowired
    PointService pointService;

    @Autowired
    LocationPointService locationPointService;

    @Autowired
    LocationAddInfoService locationAddInfoService;

    @GetMapping()
    public ResponseEntity<List<Location>> listLocations() {
        return ResponseEntity.ok(locationService.findAll());
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<Location> getLocationDetails(@PathVariable("locationId") Integer locationId) {
        return locationService.findById(locationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{locationId}/points")
    public ResponseEntity<List<Point>> getLocationPoints(@PathVariable("locationId") Integer locationId) {
        List<LocationPoint> locationPoints = locationPointService.findAllByLocationId(locationId);

        if (locationPoints.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Point> points = locationPoints.stream()
                .map(LocationPoint::getPointId)
                .flatMap(pointId -> pointService.findAll().stream()
                        .filter(point -> point.getId().equals(pointId)))
                .distinct()
                .toList();

        return points.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(points);
    }

    @GetMapping("/{locationId}/points/{pointId}")
    public ResponseEntity<Point> getLocationPointDetails(@PathVariable("locationId") Integer locationId,
                                                         @PathVariable("pointId") Integer pointId) {
        boolean isAssociated = locationPointService.findAllByLocationId(locationId).stream()
                .anyMatch(lp -> lp.getPointId().equals(pointId));

        if (!isAssociated) {
            return ResponseEntity.notFound().build();
        }

        // Проверяем, существует ли сама точка
        return pointService.findById(pointId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{locationId}/addInfoList")
    public ResponseEntity<List<LocationAddInfo>> getLocationAddInfoList(
            @PathVariable("locationId") Integer locationId) {
        return ResponseEntity.ok(locationAddInfoService.findAllByLocationId(locationId));
    }

    @GetMapping("/{locationId}/addInfoList/{addInfoId}")
    public ResponseEntity<LocationAddInfo> getLocationAddInfoById(
            @PathVariable("locationId") Integer locationId, @PathVariable("addInfoId") Integer addInfoId) {
        boolean isAssociated = locationAddInfoService.findAllByLocationId(locationId).stream()
                        .anyMatch(lai -> lai.getId().equals(addInfoId));

        if (!isAssociated) {
            return ResponseEntity.notFound().build();
        }

        return locationAddInfoService.findById(addInfoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//    @GetMapping("/update/{locationId}")
//    public String updateLocation(@PathVariable Integer locationId, HttpSession session, Model model) {
//        Location location = locationService.findById(locationId).orElse(null);
//        assert location != null;
//        model.addAttribute("location", location);
//        session.setAttribute("locationId", locationId);
//        return "update-location"; // Название шаблона для редактирования
//    }
//
//    @GetMapping("/delete/{locationId}")
//    public String deleteLocation(@PathVariable Integer locationId, Model model) {
//        Location location = locationService.findById(locationId).orElse(null);
//        model.addAttribute("location", location);
//        return "nots/del_location_ask";
//    }
}
