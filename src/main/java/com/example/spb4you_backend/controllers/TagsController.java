package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.Location;
import com.example.spb4you_backend.models.Route;
import com.example.spb4you_backend.models.Tag;
import com.example.spb4you_backend.services.LocationService;
import com.example.spb4you_backend.services.RouteService;
import com.example.spb4you_backend.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/tags")
public class TagsController {

    @Autowired
    private TagService tagService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private RouteService routeService;

    @GetMapping()
    public ResponseEntity<List<Tag>> listTags() {
        return ResponseEntity.ok(tagService.findAll());
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<Tag> getCategory(@PathVariable("tagId") Integer tagId) {
        return tagService.findById(tagId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{tagId}/locations")
    public ResponseEntity<List<Location>> getLocationsByTag(@PathVariable("tagId") Integer tagId) {
        return tagService.findById(tagId)
                .map(category -> {
                    List<Location> locations = locationService.findAll().stream()
                            .filter(location -> location.getTags().contains(tagId))
                            .toList();
                    return ResponseEntity.ok(locations);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{tagId}/routes")
    public ResponseEntity<List<Route>> getRoutesByTag(@PathVariable("tagId") Integer tagId) {
        return tagService.findById(tagId)
                .map(category -> {
                    List<Route> routes = routeService.findAll().stream()
                            .filter(route -> route.getTags().contains(tagId))
                            .toList();
                    return ResponseEntity.ok(routes);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
