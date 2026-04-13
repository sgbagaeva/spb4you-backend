package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.links.PointPhoto;
import com.example.spb4you_backend.repositories.PointPhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PointPhotoService extends GenericService<PointPhoto, Integer> {
    private final PointPhotoRepository pointPhotoRepository;

    public PointPhotoService(PointPhotoRepository pointPhotoRepository) {
        super(pointPhotoRepository);
        this.pointPhotoRepository = pointPhotoRepository;
    }

    /**
     * Получение всех фото для точки
     */
    public Optional<PointPhoto> findByPointIdAndPhotoId(Integer pointId, Integer photoId) {
        return pointPhotoRepository.findByPointIdAndPhotoId(pointId, photoId);
    }


    /**
     * Получение всех фото для точки
     */
    public List<PointPhoto> findAllByPointId(Integer pointId) {
        return pointPhotoRepository.findAllByPointId(pointId);
    }


    /**
     * Удаление всех фото для точки
     */
    @Transactional
    public void deleteByPointId(Integer pointId) {
        pointPhotoRepository.deleteByPointId(pointId);
    }

    /**
     * Получение всех связей по ID фото
     */
    public List<PointPhoto> findAllByPhotoId(Integer photoId) {
        return pointPhotoRepository.findAllByPhotoId(photoId);
    }

    /**
     * Удаление всех связей по ID фото
     */
    @Transactional
    public void deleteByPhotoId(Integer photoId) {
        pointPhotoRepository.deleteByPhotoId(photoId);
    }

    /**
     * Сохранение фото для точки с указанием порядка сортировки
     */
    @Transactional
    public PointPhoto savePointPhoto(Integer pointId, Integer photoId, Integer sortOrder) {
        PointPhoto pointPhoto = new PointPhoto(pointId, photoId, Objects.requireNonNullElse(sortOrder, 0));
        return pointPhotoRepository.save(pointPhoto);
    }

    /**
     * Сохранение нескольких фото для точки с порядком сортировки
     */
    @Transactional
    public void savePointPhotos(Integer pointId, List<Integer> photoIds) {
        // Сначала удаляем старые связи
        deleteByPointId(pointId);

        // Сохраняем новые с порядком сортировки
        for (int i = 0; i < photoIds.size(); i++) {
            savePointPhoto(pointId, photoIds.get(i), i);
        }
    }
}