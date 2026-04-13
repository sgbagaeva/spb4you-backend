package com.example.spb4you_backend.repositories;

import com.example.spb4you_backend.models.AdditionalInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalInfoRepository extends CrudRepository<AdditionalInfo, Integer> {
}