package com.example.spb4you_backend.repositories;

import com.example.spb4you_backend.models.links.RouteAdditionalInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteAdditionalInfoRepository extends CrudRepository<RouteAdditionalInfo, Integer> {
    List<RouteAdditionalInfo> findAllByRouteId(Integer routeId);

    void deleteByRouteId(Integer routeId);

    Optional<RouteAdditionalInfo> findByRouteIdAndAdditionalInfoId(Integer routeId, Integer additionalInfoId);
    void deleteByRouteIdAndAdditionalInfoId(Integer routeId, Integer additionalInfoId);
    boolean existsByAdditionalInfoId(Integer additionalInfoId);
}
