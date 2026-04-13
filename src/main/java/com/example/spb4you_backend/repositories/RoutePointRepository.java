package com.example.spb4you_backend.repositories;

import com.example.spb4you_backend.models.links.RoutePoint;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutePointRepository extends CrudRepository<RoutePoint, Integer> {

    List<RoutePoint> findAllByRouteId(Integer routeId);

    List<RoutePoint> findAllByPointId(Integer pointId);

    void deleteByRouteId(Integer routeId);

    void deleteByPointId(Integer pointId);

    boolean existsByRouteIdAndPointId(Integer routeId, Integer pointId);

    Optional<RoutePoint> findByRouteIdAndPointId(Integer routeId, Integer pointId);
    void deleteByRouteIdAndPointId(Integer routeId, Integer pointId);
}
