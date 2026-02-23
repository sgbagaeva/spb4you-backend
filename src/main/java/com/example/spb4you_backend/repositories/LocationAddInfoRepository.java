package com.example.spb4you_backend.repositories;

import com.example.spb4you_backend.models.LocationAddInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationAddInfoRepository extends CrudRepository<LocationAddInfo, Integer> {
    List<LocationAddInfo> findAllByLocationId(Integer locationId);
}
