package com.example.spb4you_backend.repositories;

import com.example.spb4you_backend.models.PointPhoto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointPhotoRepository extends CrudRepository<PointPhoto, Integer> {

    List<PointPhoto> findAllByPointId(Integer pointId);

}
