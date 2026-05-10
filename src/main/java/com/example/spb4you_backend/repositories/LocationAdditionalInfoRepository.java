package com.example.spb4you_backend.repositories;

import com.example.spb4you_backend.models.links.LocationAdditionalInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationAdditionalInfoRepository extends CrudRepository<LocationAdditionalInfo, Integer> {

    List<LocationAdditionalInfo> findAllByLocationId(Integer locationId);

    void deleteByLocationId(Integer locationId);

    Optional<LocationAdditionalInfo> findByLocationIdAndAdditionalInfoId(
                Integer locationId, Integer additionalInfoId);
    void deleteByLocationIdAndAdditionalInfoId(Integer locationId, Integer additionalInfoId);
    boolean existsByAdditionalInfoId(Integer additionalInfoId);
}