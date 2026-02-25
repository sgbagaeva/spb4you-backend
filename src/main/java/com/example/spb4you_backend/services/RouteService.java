package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.Route;
import com.example.spb4you_backend.repositories.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RouteService extends GenericService<Route, Integer> {
    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        super(routeRepository);
        this.routeRepository = routeRepository;
    }

    // Метод для поиска маршрута по названию
    public Optional<Route> findByName(String name) {
        return routeRepository.findByName(name);
    }
}

