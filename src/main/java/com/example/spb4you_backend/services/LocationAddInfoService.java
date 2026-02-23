package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.LocationAddInfo;
import com.example.spb4you_backend.repositories.LocationAddInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationAddInfoService extends GenericService<LocationAddInfo, Integer> {
    private final LocationAddInfoRepository locationAddInfoRepository;

    public LocationAddInfoService(LocationAddInfoRepository locationAddInfoRepository) {
        super(locationAddInfoRepository);
        this.locationAddInfoRepository = locationAddInfoRepository;
    }

    public List<LocationAddInfo> findAllByLocationId(Integer locationId) {
        return locationAddInfoRepository.findAllByLocationId(locationId);
    }
}
