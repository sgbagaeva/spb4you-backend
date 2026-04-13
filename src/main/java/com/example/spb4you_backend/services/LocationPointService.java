package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.links.LocationPoint;
import com.example.spb4you_backend.repositories.LocationPointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LocationPointService extends GenericService<LocationPoint, Integer> {
    private final LocationPointRepository locationPointRepository;

    public LocationPointService(LocationPointRepository locationPointRepository) {
        super(locationPointRepository);
        this.locationPointRepository = locationPointRepository;
    }

    /**
     * Получение всех связей по ID локации
     */
    public List<LocationPoint> findAllByLocationId(Integer locationId) {
        return locationPointRepository.findAllByLocationId(locationId);
    }

    /**
     * Получение всех связей по ID точки
     */
    public List<LocationPoint> findAllByPointId(Integer pointId) {
        return locationPointRepository.findAllByPointId(pointId);
    }

    /**
     * Удаление всех связей для локации
     */
    @Transactional
    public void deleteByLocationId(Integer locationId) {
        locationPointRepository.deleteByLocationId(locationId);
    }

    /**
     * Удаление всех связей для точки
     */
    @Transactional
    public void deleteByPointId(Integer pointId) {
        locationPointRepository.deleteByPointId(pointId);
    }

    /**
     * Получение ID локации по ID точки
     */
//    public Optional<Integer> findLocationIdByPointId(Integer pointId) {
//        return locationPointRepository.findByPointId(pointId)
//                .map(LocationPoint::getLocationId);
//    }

    /**
     * Проверка существования связи
     */
//    public boolean existsByLocationIdAndPointId(Integer locationId, Integer pointId) {
//        return locationPointRepository.existsByLocationIdAndPointId(locationId, pointId);
//    }

    /**
     * Сохранение связи
     */
    @Transactional
    public LocationPoint saveLink(Integer locationId, Integer pointId) {
        LocationPoint link = new LocationPoint(locationId, pointId);
        return locationPointRepository.save(link);
    }

    /**
     * Обновление всех точек для локации
     */
    @Transactional
    public void updateLocationPoints(Integer locationId, List<Integer> pointIds) {
        // Удаляем старые связи
        deleteByLocationId(locationId);

        // Создаем новые
        for (Integer pointId : pointIds) {
            saveLink(locationId, pointId);
        }
    }
}