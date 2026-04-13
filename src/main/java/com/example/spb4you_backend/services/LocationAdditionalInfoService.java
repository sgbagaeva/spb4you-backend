package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.links.LocationAdditionalInfo;
import com.example.spb4you_backend.repositories.LocationAdditionalInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationAdditionalInfoService extends GenericService<LocationAdditionalInfo, Integer> {
    private final LocationAdditionalInfoRepository locationAdditionalInfoRepository;

    public LocationAdditionalInfoService(LocationAdditionalInfoRepository locationAdditionalInfoRepository) {
        super(locationAdditionalInfoRepository);
        this.locationAdditionalInfoRepository = locationAdditionalInfoRepository;
    }

    public List<LocationAdditionalInfo> findAllByLocationId(Integer locationId) {
        return locationAdditionalInfoRepository.findAllByLocationId(locationId);
    }

    @Transactional
    public void deleteByLocationId(Integer locationId) {
        locationAdditionalInfoRepository.deleteByLocationId(locationId);
    }

    @Transactional
    public void saveLink(Integer locationId, Integer additionalInfoId, Integer sortOrder) {
        LocationAdditionalInfo link = new LocationAdditionalInfo(
                locationId, additionalInfoId, sortOrder != null ? sortOrder : 0);
        locationAdditionalInfoRepository.save(link);
    }

    @Transactional
    public void updateSortOrders(Integer locationId, List<Integer> additionalInfoIds) {
        deleteByLocationId(locationId);
        for (int i = 0; i < additionalInfoIds.size(); i++) {
            saveLink(locationId, additionalInfoIds.get(i), i);
        }
    }
}