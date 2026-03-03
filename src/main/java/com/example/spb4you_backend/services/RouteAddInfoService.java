package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.RouteAddInfo;
import com.example.spb4you_backend.repositories.RouteAddInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteAddInfoService extends GenericService<RouteAddInfo, Integer> {
    private final RouteAddInfoRepository routeAddInfoRepository;

    public RouteAddInfoService(RouteAddInfoRepository routeAddInfoRepository) {
        super(routeAddInfoRepository);
        this.routeAddInfoRepository = routeAddInfoRepository;
    }

    public List<RouteAddInfo> findAllByRouteId(Integer routeId) {
        return routeAddInfoRepository.findAllByRouteId(routeId);
    }
}
