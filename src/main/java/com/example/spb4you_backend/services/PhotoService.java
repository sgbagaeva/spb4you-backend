package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.Location;
import com.example.spb4you_backend.models.Photo;
import com.example.spb4you_backend.repositories.PhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PhotoService extends GenericService<Photo, Integer> {
    private final PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        super(photoRepository);
        this.photoRepository = photoRepository;
    }

    @Transactional(readOnly = true)
    public Photo getPhotoById(Integer id) {
        Optional<Photo> locationOpt = photoRepository.findById(id);
        if (locationOpt.isEmpty()) {
            throw new RuntimeException("Фото не найдено с ID: " + id);
        }

        return locationOpt.get();
    }


}
