package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.links.RouteAdditionalInfo;
import com.example.spb4you_backend.repositories.RouteAdditionalInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RouteAdditionalInfoService extends GenericService<RouteAdditionalInfo, Integer> {
    private final RouteAdditionalInfoRepository routeAdditionalInfoRepository;

    public RouteAdditionalInfoService(RouteAdditionalInfoRepository routeAdditionalInfoRepository) {
        super(routeAdditionalInfoRepository);
        this.routeAdditionalInfoRepository = routeAdditionalInfoRepository;
    }

    public List<RouteAdditionalInfo> findAllByRouteId(Integer routeId) {
        return routeAdditionalInfoRepository.findAllByRouteId(routeId);
    }

    @Transactional
    public void deleteByRouteId(Integer routeId) {
        routeAdditionalInfoRepository.deleteByRouteId(routeId);
    }

    @Transactional
    public void saveLink(Integer routeId, Integer additionalInfoId, Integer sortOrder) {
        RouteAdditionalInfo link = new RouteAdditionalInfo(
                routeId, additionalInfoId, sortOrder != null ? sortOrder : 0);
        routeAdditionalInfoRepository.save(link);
    }

    @Transactional
    public void updateSortOrders(Integer routeId, List<Integer> additionalInfoIds) {
        deleteByRouteId(routeId);
        for (int i = 0; i < additionalInfoIds.size(); i++) {
            saveLink(routeId, additionalInfoIds.get(i), i);
        }
    }
}
