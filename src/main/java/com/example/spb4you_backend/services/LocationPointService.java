package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.LocationPoint;
import com.example.spb4you_backend.repositories.LocationPointRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationPointService extends GenericService <LocationPoint, Integer>{
    private final LocationPointRepository locationPointRepository;

    public LocationPointService(LocationPointRepository locationPointRepository) {
        super(locationPointRepository);
        this.locationPointRepository = locationPointRepository;
    }

    public List<LocationPoint> findAllByLocationId(Integer locationId) {
        return locationPointRepository.findAllByLocationId(locationId);
    }

    public List<LocationPoint> findAllByPointId(Integer pointId) {
        return locationPointRepository.findAllByPointId(pointId);
    }
}
