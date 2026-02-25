package com.example.spb4you_backend.repositories;

import com.example.spb4you_backend.models.Route;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteRepository extends CrudRepository<Route, Integer> {
    // Дополнительные методы для Route, если нужно
    Optional<Route> findByName(String name);
}

