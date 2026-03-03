package com.example.spb4you_backend.repositories;

import com.example.spb4you_backend.models.LocationAddInfo;
import com.example.spb4you_backend.models.RouteAddInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteAddInfoRepository extends CrudRepository<RouteAddInfo, Integer> {
    List<RouteAddInfo> findAllByRouteId(Integer routeId);
}
