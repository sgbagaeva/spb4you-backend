package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.links.RoutePoint;
import com.example.spb4you_backend.repositories.RoutePointRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutePointService extends GenericService <RoutePoint, Integer>{
    private final RoutePointRepository routePointRepository;

    public RoutePointService(RoutePointRepository routePointRepository) {
        super(routePointRepository);
        this.routePointRepository = routePointRepository;
    }

    public List<RoutePoint> findAllByRouteId(Integer routeId) {
        return routePointRepository.findAllByRouteId(routeId);
    }

    public List<RoutePoint> findAllByPointId(Integer pointId) {
        return routePointRepository.findAllByPointId(pointId);
    }
}