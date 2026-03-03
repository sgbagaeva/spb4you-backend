package com.example.spb4you_backend.repositories;

import com.example.spb4you_backend.models.RoutePoint;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutePointRepository extends CrudRepository<RoutePoint, Integer> {

    List<RoutePoint> findAllByRouteId(Integer routeId);

    List<RoutePoint> findAllByPointId(Integer pointId);
}
