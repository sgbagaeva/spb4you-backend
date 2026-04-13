package com.example.spb4you_backend.repositories;

import com.example.spb4you_backend.models.links.PointPhoto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointPhotoRepository extends CrudRepository<PointPhoto, Integer> {

    Optional<PointPhoto> findByPointIdAndPhotoId(Integer pointId, Integer photoId);

    List<PointPhoto> findAllByPointId(Integer pointId);

    void deleteByPointId(Integer pointId);

    List<PointPhoto> findAllByPhotoId(Integer photoId);

    void deleteByPhotoId(Integer photoId);

    boolean existsByPhotoId(Integer photoId);
}