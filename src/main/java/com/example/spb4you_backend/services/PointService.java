package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.Point;
import com.example.spb4you_backend.repositories.PointRepository;
import org.springframework.stereotype.Service;

@Service
public class PointService extends GenericService<Point, Integer> {
    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        super(pointRepository);
        this.pointRepository = pointRepository;
    }

}

