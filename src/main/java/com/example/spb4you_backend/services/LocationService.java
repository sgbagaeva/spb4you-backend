package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.Location;
import com.example.spb4you_backend.repositories.LocationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService extends GenericService<Location, Integer> {
    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        super(locationRepository);
        this.locationRepository = locationRepository;
    }

    // Метод для поиска локации по названию
    public Optional<Location> findByName(String name) {
        return locationRepository.findByName(name);
    }
}
