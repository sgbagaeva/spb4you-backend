package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.PointPhoto;
import com.example.spb4you_backend.repositories.PointPhotoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointPhotoService extends GenericService<PointPhoto, Integer> {
    private final PointPhotoRepository pointPhotoRepository;

    public PointPhotoService(PointPhotoRepository pointPhotoRepository) {
        super(pointPhotoRepository);
        this.pointPhotoRepository = pointPhotoRepository;
    }

    public List<PointPhoto> findAllByPointId(Integer pointId) {
        return pointPhotoRepository.findAllByPointId(pointId);
    }

}
