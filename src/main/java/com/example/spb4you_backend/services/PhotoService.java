package com.example.spb4you_backend.services;
import com.example.spb4you_backend.repositories.PhotoRepository;
import org.springframework.stereotype.Service;

import com.example.spb4you_backend.models.Photo;

import java.util.Optional;

@Service
public class PhotoService extends GenericService<Photo, Integer> {
    private final PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        super(photoRepository);
        this.photoRepository = photoRepository;
    }

    // Метод для поиска фотографии по имени
    public Optional<Photo> findByName(String name) {
        return photoRepository.findByName(name);
    }
}
