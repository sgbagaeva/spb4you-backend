package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

    @Value("${vkcloud.s3.bucket-name}")
    private String bucketName;

    @Value("${vkcloud.s3.endpoint}")
    private String endpoint;

    @Autowired
    private S3Client s3Client;

    /**
     * Сохраняет основную фотографию локации (загрузка сразу в облако)
     * @param locationId Id локации
     * @param file файл для загрузки
     * @return Photo с информацией о загруженном фото
     */
    public Photo saveLocationMainPhoto(Integer locationId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.info("Файл для загрузки отсутствует");
            return null;
        }

        logger.info("Начинаем загрузку основного фото для локации ID: {}", locationId);

        try {
            Photo photo = uploadFileAndCreatePhoto(file, "locations", locationId);
            logger.debug("Основное фото загружено: {}", photo.getFileKey());
            return photo;
        } catch (Exception e) {
            logger.error("Ошибка при загрузке основного фото для локации ID {}: {}", locationId, e.getMessage());
            return null;
        }
    }

    /**
     * Сохраняет фотографии локации (загрузка сразу в облако)
     * @param locationId ID локации
     * @param files список файлов для загрузки
     * @return список Photo с информацией о загруженных фото
     */
    public List<Photo> saveLocationPhotos(Integer locationId, List<MultipartFile> files) {
        List<Photo> uploadedPhotos = new ArrayList<>();

        if (files == null || files.isEmpty()) {
            logger.info("Нет файлов для загрузки");
            return uploadedPhotos;
        }

        logger.info("Начинаем загрузку {} фото для локации ID: {}", files.size(), locationId);

        for (MultipartFile file : files) {
            try {
                Photo photo = uploadFileAndCreatePhoto(file, "locations", locationId);
                uploadedPhotos.add(photo);
                logger.debug("Фото загружено: {}", photo.getFileKey());
            } catch (Exception e) {
                logger.error("Ошибка при загрузке файла {}: {}", file.getOriginalFilename(), e.getMessage());
            }
        }

        logger.info("Успешно загружено {} из {} фото", uploadedPhotos.size(), files.size());
        return uploadedPhotos;
    }

    /**
     * Сохраняет фотографии для конкретной точки
     * @param pointId ID точки
     * @param files список файлов
     * @return список Photo
     */
    public List<Photo> savePointPhotos(Integer pointId, List<MultipartFile> files) {
        List<Photo> uploadedPhotos = new ArrayList<>();

        if (files == null || files.isEmpty()) {
            return uploadedPhotos;
        }

        logger.info("Загрузка {} фото для точки ID: {}", files.size(), pointId);

        for (MultipartFile file : files) {
            try {
                Photo photo = uploadFileAndCreatePhoto(file, "points", pointId);
                uploadedPhotos.add(photo);
            } catch (Exception e) {
                logger.error("Ошибка при загрузке фото точки {}: {}", pointId, e.getMessage());
            }
        }

        return uploadedPhotos;
    }

    /**
     * Загрузка файла в S3 и создание Photo
     */
    private Photo uploadFileAndCreatePhoto(MultipartFile file, String type, Integer entityId) throws IOException {
        // Генерируем уникальное имя файла
        String fileName = generateFileName(type, entityId, file.getOriginalFilename());

        // Загружаем файл в S3
        String fileKey = uploadFileToS3(file, fileName);

        // Создаем и заполняем Photo
        Photo photo = new Photo();
        photo.setFilename(file.getOriginalFilename());
        photo.setSize(file.getSize());
        photo.setMimeType(file.getContentType());
        photo.setFileKey(fileKey);

        return photo;
    }

    /**
     * Загрузка файла в S3 и возврат ключа файла
     */
    private String uploadFileToS3(MultipartFile file, String fileName) throws IOException {
        logger.debug("Загрузка файла в S3: {}", fileName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)  // Делаем файл публичным
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        logger.debug("Файл успешно загружен: {}", fileName);

        return fileName;
    }

    /**
     * Получение публичного URL для доступа к файлу
     */
    public String getFileUrl(String fileKey) {
        return String.format("%s/%s/%s", endpoint, bucketName, fileKey);
    }

    /**
     * Удаление файла из S3 по ключу
     */
    public void deleteFile(String fileKey) {
        try {
            if (fileKey == null || fileKey.isEmpty()) {
                logger.warn("Ключ файла пустой, пропускаем удаление");
                return;
            }

            logger.info("Удаление файла из S3: {}", fileKey);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            logger.info("Файл успешно удален: {}", fileKey);

        } catch (Exception e) {
            logger.error("Ошибка при удалении файла: {}", e.getMessage(), e);
        }
    }

    /**
     * Генерация уникального имени файла
     * Формат: {type}/{id}/{uuid}_{timestamp}{extension}
     */
    private String generateFileName(String type, Integer id, String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = String.format("%s/%d/%s_%d%s",
                type,
                id,
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                extension);

        logger.debug("Сгенерировано имя файла: {}", fileName);
        return fileName;
    }
}