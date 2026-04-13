package com.example.spb4you_backend.services;


import com.example.spb4you_backend.models.links.RouteAdditionalInfo;
import com.example.spb4you_backend.repositories.RouteAdditionalInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteAdditionalInfoService extends GenericService<RouteAdditionalInfo, Integer> {
    private final RouteAdditionalInfoRepository routeAdditInfoRepository;

    public RouteAdditionalInfoService(RouteAdditionalInfoRepository routeAdditInfoRepository) {
        super(routeAdditInfoRepository);
        this.routeAdditInfoRepository = routeAdditInfoRepository;
    }

    public List<RouteAdditionalInfo> findAllByRouteId(Integer routeId) {
        return routeAdditInfoRepository.findAllByRouteId(routeId);
    }
}
