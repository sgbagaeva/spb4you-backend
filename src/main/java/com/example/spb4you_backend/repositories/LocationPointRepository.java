package com.example.spb4you_backend.repositories;

import com.example.spb4you_backend.models.links.LocationPoint;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationPointRepository extends CrudRepository<LocationPoint, Integer> {

    List<LocationPoint> findAllByLocationId(Integer locationId);

    List<LocationPoint> findAllByPointId(Integer pointId);

    void deleteByLocationId(Integer locationId);

    void deleteByPointId(Integer pointId);

    boolean existsByLocationIdAndPointId(Integer locationId, Integer pointId);

    Optional<LocationPoint> findByLocationIdAndPointId(Integer locationId, Integer pointId);
    void deleteByLocationIdAndPointId(Integer locationId, Integer pointId);
}
