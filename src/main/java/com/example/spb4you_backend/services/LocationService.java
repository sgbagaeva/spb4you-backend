package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.*;
import com.example.spb4you_backend.models.links.LocationAdditionalInfo;
import com.example.spb4you_backend.models.links.LocationPoint;
import com.example.spb4you_backend.models.links.PointPhoto;
import com.example.spb4you_backend.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LocationService extends GenericService<Location, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);
    private final LocationRepository locationRepository;
    private final PointRepository pointRepository;
    private final PhotoRepository photoRepository;
    private final AdditionalInfoRepository additionalInfoRepository;
    private final LocationAdditionalInfoRepository locationAdditionalInfoRepository;
    private final LocationPointRepository locationPointRepository;
    private final PointPhotoRepository pointPhotoRepository;
    private final StorageService storageService;

    public LocationService(LocationRepository locationRepository,
                           PointRepository pointRepository,
                           PhotoRepository photoRepository,
                           AdditionalInfoRepository additionalInfoRepository,
                           LocationAdditionalInfoRepository locationAdditionalInfoRepository,
                           LocationPointRepository locationPointRepository,
                           PointPhotoRepository pointPhotoRepository,
                           StorageService storageService) {
        super(locationRepository);
        this.locationRepository = locationRepository;
        this.pointRepository = pointRepository;
        this.photoRepository = photoRepository;
        this.additionalInfoRepository = additionalInfoRepository;
        this.locationAdditionalInfoRepository = locationAdditionalInfoRepository;
        this.locationPointRepository = locationPointRepository;
        this.pointPhotoRepository = pointPhotoRepository;
        this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public Location getLocationById(Integer id) {
        Optional<Location> locationOpt = locationRepository.findById(id);
        if (locationOpt.isEmpty()) {
            throw new RuntimeException("Локация не найдена с ID: " + id);
        }
        Location location = locationOpt.get();

        loadLocationRelations(location);

        // Заполняем URL основной фотографии
        if (location.getMainPhotoId() != null) {
            Optional<Photo> mainPhotoOpt = photoRepository.findById(location.getMainPhotoId());
            if (mainPhotoOpt.isPresent()) {
                String url = storageService.getFileUrl(mainPhotoOpt.get().getFileKey());
                location.setMainPhotoUrl(url);
            }
        }

        return location;
    }

    @Transactional(readOnly = true)
    public List<Location> getAllLocations() {
        List<Location> locations = (List<Location>) locationRepository.findAll();
        for (Location location : locations) {
            loadLocationRelations(location);

            // Заполняем URL основной фотографии
            if (location.getMainPhotoId() != null) {
                Optional<Photo> mainPhotoOpt = photoRepository.findById(location.getMainPhotoId());
                if (mainPhotoOpt.isPresent()) {
                    String url = storageService.getFileUrl(mainPhotoOpt.get().getFileKey());
                    location.setMainPhotoUrl(url);
                }
            }
        }
        return locations;
    }

    @Transactional(readOnly = true)
    public Photo getMainPhoto(Integer locationId) {
        Location location = getLocationById(locationId);
        if (location.getMainPhotoId() == null) {
            return null;
        }

        Optional<Photo> photoOpt = photoRepository.findById(location.getMainPhotoId());
        if (photoOpt.isPresent()) {
            Photo photo = photoOpt.get();
            try {
                String url = storageService.getFileUrl(photo.getFileKey());
                photo.setUrl(url);
                return photo;
            } catch (Exception e) {
                logger.error("Ошибка получения URL для основной фотографии {}: {}", photo.getId(), e.getMessage());
                // Если файла нет в облаке, удаляем запись из БД и сбрасываем mainPhotoId
                photoRepository.deleteById(photo.getId());
                location.setMainPhotoId(null);
                locationRepository.save(location);
                return null;
            }
        }
        return null;
    }

    private void loadLocationRelations(Location location) {
        // Загружаем дополнительную информацию
        List<LocationAdditionalInfo> locationInfo = locationAdditionalInfoRepository
                .findAllByLocationId(location.getId());
        List<AdditionalInfo> additionalInfoList = new ArrayList<>();
        for (LocationAdditionalInfo li : locationInfo) {
            Optional<AdditionalInfo> infoOpt = additionalInfoRepository.findById(li.getAdditionalInfoId());
            if (infoOpt.isPresent()) {
                AdditionalInfo info = infoOpt.get();
                additionalInfoList.add(info);
            }
        }
        location.setAdditionalInfo(additionalInfoList);

        // Загружаем точки
        List<LocationPoint> locationPoints = locationPointRepository.findAllByLocationId(location.getId());
        List<Point> points = new ArrayList<>();
        for (LocationPoint lp : locationPoints) {
            Optional<Point> pointOpt = pointRepository.findById(lp.getPointId());
            if (pointOpt.isPresent()) {
                Point point = pointOpt.get();
                List<PointPhoto> pointPhotos = pointPhotoRepository.findAllByPointId(point.getId());
                List<Photo> pointPhotoList = new ArrayList<>();
                for (PointPhoto pp : pointPhotos) {
                    Optional<Photo> photoOpt = photoRepository.findById(pp.getPhotoId());
                    if (photoOpt.isPresent()) {
                        Photo photo = photoOpt.get();
                        try {
                            String url = storageService.getFileUrl(photo.getFileKey());
                            photo.setUrl(url);
                            pointPhotoList.add(photo);
                        } catch (Exception e) {
                            logger.error("Ошибка получения URL для фото {}: {}", photo.getId(), e.getMessage());
                            // Если файла нет в облаке, удаляем запись из БД
                            photoRepository.deleteById(photo.getId());
                            logger.warn("Удалена запись о несуществующем фото ID: {}", photo.getId());
                        }
                    }
                }
                point.setPhotos(pointPhotoList);
                points.add(point);
            }
        }
        location.setPoints(points);

        // Загружаем общие фото (исключая основную)
        List<Photo> photos = new ArrayList<>();
        if (location.getPhotoIds() != null) {
            for (Integer photoId : location.getPhotoIds()) {
                if (location.getMainPhotoId() != null && photoId.equals(location.getMainPhotoId())) {
                    continue;
                }
                Optional<Photo> photoOpt = photoRepository.findById(photoId);
                if (photoOpt.isPresent()) {
                    Photo photo = photoOpt.get();
                    try {
                        String url = storageService.getFileUrl(photo.getFileKey());
                        photo.setUrl(url);
                        photos.add(photo);
                    } catch (Exception e) {
                        logger.error("Ошибка получения URL для фото {}: {}", photo.getId(), e.getMessage());
                        // Если файла нет в облаке, удаляем запись из БД
                        photoRepository.deleteById(photo.getId());
                        // Также удаляем ID из списка photoIds
                        location.getPhotoIds().remove(photoId);
                        logger.warn("Удалена запись о несуществующем фото ID: {}", photo.getId());
                    }
                }
            }
        }
        location.setPhotos(photos);
    }

    @Transactional
    public Location createLocation(Location location) {
        logger.info("=== СОЗДАНИЕ ЛОКАЦИИ ===");

        if (locationRepository.findByName(location.getName()).isPresent()) {
            throw new RuntimeException("Локация с таким названием уже существует");
        }

        Location savedLocation = new Location();
        savedLocation.setName(location.getName());
        savedLocation.setDescription(location.getDescription());
        savedLocation.setCategoryIds(new ArrayList<>());
        savedLocation.setTagIds(new ArrayList<>());
        savedLocation.setPhotoIds(new ArrayList<>());
        savedLocation.setLikes(0);

        savedLocation = locationRepository.save(savedLocation);
        logger.info("Создана локация с ID: {}", savedLocation.getId());

        // Сохраняем дополнительную информацию
        if (location.getAdditionalInfo() != null && !location.getAdditionalInfo().isEmpty()) {
            for (int i = 0; i < location.getAdditionalInfo().size(); i++) {
                AdditionalInfo info = location.getAdditionalInfo().get(i);

                AdditionalInfo savedInfo = new AdditionalInfo();
                savedInfo.setTitle(info.getTitle());
                savedInfo.setDescription(info.getDescription() != null ? info.getDescription() : "");
                savedInfo = additionalInfoRepository.save(savedInfo);

                LocationAdditionalInfo link = new LocationAdditionalInfo();
                link.setLocationId(savedLocation.getId());
                link.setAdditionalInfoId(savedInfo.getId());
                link.setSortOrder(i);
                locationAdditionalInfoRepository.save(link);
            }
        }

        // Сохраняем точки
        if (location.getPoints() != null && !location.getPoints().isEmpty()) {
            for (int i = 0; i < location.getPoints().size(); i++) {
                Point point = location.getPoints().get(i);

                Point savedPoint = new Point();
                savedPoint.setName(point.getName() != null ? point.getName() : "");
                savedPoint.setDescription(point.getDescription() != null ? point.getDescription() : "");
                savedPoint.setLatitude(point.getLatitude());
                savedPoint.setLongitude(point.getLongitude());
                savedPoint = pointRepository.save(savedPoint);

                LocationPoint link = new LocationPoint();
                link.setLocationId(savedLocation.getId());
                link.setPointId(savedPoint.getId());
                locationPointRepository.save(link);

                if (point.getPhotos() != null && !point.getPhotos().isEmpty()) {
                    for (int j = 0; j < point.getPhotos().size(); j++) {
                        Photo photo = point.getPhotos().get(j);
                        if (photo.getId() != null) {
                            PointPhoto pointPhoto = new PointPhoto(savedPoint.getId(), photo.getId(), j);
                            pointPhotoRepository.save(pointPhoto);
                        }
                    }
                }
            }
        }

        return getLocationById(savedLocation.getId());
    }

    @Transactional
    public Location updateLocation(Integer id, Location location) {
        logger.info("=== ОБНОВЛЕНИЕ ЛОКАЦИИ ID: {} ===", id);

        Optional<Location> locationOpt = locationRepository.findById(id);
        if (locationOpt.isEmpty()) {
            throw new RuntimeException("Локация не найдена с ID: " + id);
        }
        Location existingLocation = locationOpt.get();

        existingLocation.setName(location.getName());
        existingLocation.setDescription(location.getDescription());

        // Обновляем mainPhotoId только если он передан (не null)
        if (location.getMainPhotoId() != null) {
            existingLocation.setMainPhotoId(location.getMainPhotoId());
        }

        locationRepository.save(existingLocation);

        // Обновляем дополнительную информацию
        locationAdditionalInfoRepository.deleteByLocationId(id);

        if (location.getAdditionalInfo() != null && !location.getAdditionalInfo().isEmpty()) {
            for (int i = 0; i < location.getAdditionalInfo().size(); i++) {
                AdditionalInfo info = location.getAdditionalInfo().get(i);

                AdditionalInfo savedInfo;
                if (info.getId() != null) {
                    Optional<AdditionalInfo> existingInfo = additionalInfoRepository.findById(info.getId());
                    if (existingInfo.isPresent()) {
                        savedInfo = existingInfo.get();
                        savedInfo.setTitle(info.getTitle());
                        savedInfo.setDescription(info.getDescription());
                        savedInfo = additionalInfoRepository.save(savedInfo);
                    } else {
                        savedInfo = new AdditionalInfo();
                        savedInfo.setTitle(info.getTitle());
                        savedInfo.setDescription(info.getDescription());
                        savedInfo = additionalInfoRepository.save(savedInfo);
                    }
                } else {
                    savedInfo = new AdditionalInfo();
                    savedInfo.setTitle(info.getTitle());
                    savedInfo.setDescription(info.getDescription());
                    savedInfo = additionalInfoRepository.save(savedInfo);
                }

                LocationAdditionalInfo link = new LocationAdditionalInfo();
                link.setLocationId(id);
                link.setAdditionalInfoId(savedInfo.getId());
                link.setSortOrder(i);
                locationAdditionalInfoRepository.save(link);
            }
        }

        updatePoints(id, location.getPoints());

        return getLocationById(id);
    }

    private void updatePoints(Integer locationId, List<Point> newPoints) {
        List<LocationPoint> existingLinks = locationPointRepository.findAllByLocationId(locationId);

        // Если newPoints == null, ничего не делаем
        if (newPoints == null) {
            return;
        }

        // Получаем ID существующих точек
        List<Integer> existingPointIds = existingLinks.stream()
                .map(LocationPoint::getPointId)
                .collect(Collectors.toList());

        // Получаем ID новых точек (которые уже есть в БД)
        List<Integer> newPointIds = newPoints.stream()
                .filter(p -> p.getId() != null)
                .map(Point::getId)
                .collect(Collectors.toList());

        // Удаляем точки, которых нет в новом списке
        for (Integer existingId : existingPointIds) {
            if (!newPointIds.contains(existingId)) {
                // Удаляем фото точки
                List<PointPhoto> pointPhotos = pointPhotoRepository.findAllByPointId(existingId);
                for (PointPhoto pp : pointPhotos) {
                    photoRepository.findById(pp.getPhotoId()).ifPresent(photo -> {
                        storageService.deleteFile(photo.getFileKey());
                        photoRepository.deleteById(photo.getId());
                    });
                }
                pointPhotoRepository.deleteByPointId(existingId);
                pointRepository.deleteById(existingId);
            }
        }

        // Обновляем или создаём точки
        for (Point point : newPoints) {
            Point savedPoint;
            if (point.getId() != null) {
                // Обновляем существующую точку
                Optional<Point> existingPoint = pointRepository.findById(point.getId());
                if (existingPoint.isPresent()) {
                    savedPoint = existingPoint.get();
                    savedPoint.setName(point.getName());
                    savedPoint.setDescription(point.getDescription());
                    savedPoint.setLatitude(point.getLatitude());
                    savedPoint.setLongitude(point.getLongitude());
                    savedPoint = pointRepository.save(savedPoint);
                } else {
                    savedPoint = createNewPoint(point);
                }
            } else {
                // Создаём новую точку
                savedPoint = createNewPoint(point);
            }

            // Проверяем, существует ли уже связь
            boolean linkExists = locationPointRepository.existsByLocationIdAndPointId(locationId, savedPoint.getId());
            if (!linkExists) {
                LocationPoint link = new LocationPoint();
                link.setLocationId(locationId);
                link.setPointId(savedPoint.getId());
                locationPointRepository.save(link);
            }
        }
    }

    private Point createNewPoint(Point point) {
        Point savedPoint = new Point();
        savedPoint.setName(point.getName() != null ? point.getName() : "");
        savedPoint.setDescription(point.getDescription() != null ? point.getDescription() : "");
        savedPoint.setLatitude(point.getLatitude());
        savedPoint.setLongitude(point.getLongitude());
        savedPoint = pointRepository.save(savedPoint);

        if (point.getPhotos() != null && !point.getPhotos().isEmpty()) {
            for (int j = 0; j < point.getPhotos().size(); j++) {
                Photo photo = point.getPhotos().get(j);
                if (photo.getId() != null) {
                    PointPhoto pointPhoto = new PointPhoto(savedPoint.getId(), photo.getId(), j);
                    pointPhotoRepository.save(pointPhoto);
                }
            }
        }
        return savedPoint;
    }

    @Transactional
    public void deleteLocation(Integer id) {
        logger.info("=== НАЧАЛО УДАЛЕНИЯ ЛОКАЦИИ ID: {} ===", id);

        try {
            // 1. Получаем локацию со всеми данными
            Location location = getLocationById(id);
            if (location == null) {
                logger.error("Локация с ID {} не найдена", id);
                throw new RuntimeException("Локация не найдена");
            }

            // 2. Сохраняем списки ID для последующего удаления
            List<Integer> photoIdsToDelete = new ArrayList<>();
            if (location.getPhotoIds() != null) {
                photoIdsToDelete.addAll(location.getPhotoIds());
            }
            if (location.getMainPhotoId() != null && !photoIdsToDelete.contains(location.getMainPhotoId())) {
                photoIdsToDelete.add(location.getMainPhotoId());
            }

            // 3. Получаем все точки локации и их фото
            List<LocationPoint> locationPoints = locationPointRepository.findAllByLocationId(id);
            List<Integer> pointIds = new ArrayList<>();
            List<Integer> pointPhotoIdsToDelete = new ArrayList<>();

            for (LocationPoint lp : locationPoints) {
                pointIds.add(lp.getPointId());
                List<PointPhoto> pointPhotos = pointPhotoRepository.findAllByPointId(lp.getPointId());
                for (PointPhoto pp : pointPhotos) {
                    pointPhotoIdsToDelete.add(pp.getPhotoId());
                }
            }

            // 4. Сохраняем ID дополнительной информации ДО удаления связей
            List<LocationAdditionalInfo> additInfoList = locationAdditionalInfoRepository
                    .findAllByLocationId(id);
            List<Integer> additionalInfoIdsToDelete = new ArrayList<>();
            for (LocationAdditionalInfo li : additInfoList) {
                additionalInfoIdsToDelete.add(li.getAdditionalInfoId());
            }
            logger.info("Найдено {} блоков дополнительной информации для удаления", additionalInfoIdsToDelete.size());

            // 5. ОЧЕНЬ ВАЖНО: Сначала убираем ссылки на фото из таблицы locations
            location.setMainPhotoId(null);
            location.setPhotoIds(new ArrayList<>());
            locationRepository.save(location);
            logger.info("Очищены ссылки на фото в локации ID: {}", id);

            // 6. Удаляем PointPhoto связи (фото точек)
            for (Integer pointId : pointIds) {
                pointPhotoRepository.deleteByPointId(pointId);
                logger.info("Удалены PointPhoto связи для точки ID: {}", pointId);
            }

            // 7. Удаляем LocationPoint связи
            locationPointRepository.deleteByLocationId(id);
            logger.info("Удалены LocationPoint связи для локации ID: {}", id);

            // 8. Удаляем LocationAdditInfo связи
            locationAdditionalInfoRepository.deleteByLocationId(id);
            logger.info("Удалены LocationAdditInfo связи для локации ID: {}", id);

            // 9. Удаляем фото точек из БД и облака
            for (Integer photoId : pointPhotoIdsToDelete) {
                Optional<Photo> photoOpt = photoRepository.findById(photoId);
                if (photoOpt.isPresent()) {
                    Photo photo = photoOpt.get();
                    try {
                        storageService.deleteFile(photo.getFileKey());
                        logger.info("Фото точки удалено из облака: {}", photo.getFileKey());
                    } catch (Exception e) {
                        logger.error("Ошибка удаления фото точки из облака: {}", e.getMessage());
                    }
                    photoRepository.deleteById(photoId);
                    logger.info("Фото точки ID: {} удалено из БД", photoId);
                }
            }

            // 10. Удаляем общие фото локации из БД и облака
            for (Integer photoId : photoIdsToDelete) {
                Optional<Photo> photoOpt = photoRepository.findById(photoId);
                if (photoOpt.isPresent()) {
                    Photo photo = photoOpt.get();
                    try {
                        storageService.deleteFile(photo.getFileKey());
                        logger.info("Общее фото удалено из облака: {}", photo.getFileKey());
                    } catch (Exception e) {
                        logger.error("Ошибка удаления общего фото из облака: {}", e.getMessage());
                    }
                    photoRepository.deleteById(photoId);
                    logger.info("Общее фото ID: {} удалено из БД", photoId);
                }
            }

            // 11. Удаляем сами точки
            for (Integer pointId : pointIds) {
                pointRepository.deleteById(pointId);
                logger.info("Точка ID: {} удалена", pointId);
            }

            // 12. Удаляем дополнительную информацию (после удаления связей)
            for (Integer infoId : additionalInfoIdsToDelete) {
                Optional<AdditionalInfo> infoOpt = additionalInfoRepository.findById(infoId);
                if (infoOpt.isPresent()) {
                    additionalInfoRepository.deleteById(infoId);
                    logger.info("AdditionalInfo ID: {} удалена", infoId);
                }
            }

            // 13. Удаляем локацию
            locationRepository.deleteById(id);
            logger.info("Локация ID: {} удалена", id);

            logger.info("=== ЛОКАЦИЯ ID: {} УСПЕШНО УДАЛЕНА ===", id);
            logger.info("Удалено фото точек: {}", pointPhotoIdsToDelete.size());
            logger.info("Удалено общих фото: {}", photoIdsToDelete.size());
            logger.info("Удалено точек: {}", pointIds.size());
            logger.info("Удалено блоков дополнительной информации: {}", additionalInfoIdsToDelete.size());

        } catch (Exception e) {
            logger.error("Ошибка при удалении локации {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Не удалось удалить локацию: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Photo uploadMainLocationPhoto(Integer locationId, MultipartFile file) {
        Location location = getLocationById(locationId);

        Photo photo = storageService.saveLocationMainPhoto(locationId, file);
        if (photo == null) {
            throw new RuntimeException("Не удалось загрузить фото");
        }

        photo = photoRepository.save(photo);
        photo.setUrl(storageService.getFileUrl(photo.getFileKey()));

        location.setMainPhotoId(photo.getId());
        if (location.getPhotoIds() == null) {
            location.setPhotoIds(new ArrayList<>());
        }
        if (!location.getPhotoIds().contains(photo.getId())) {
            location.getPhotoIds().add(photo.getId());
        }
        locationRepository.save(location);

        return photo;
    }

    @Transactional
    public List<Photo> uploadLocationPhotos(Integer locationId, List<MultipartFile> files) {
        Location location = getLocationById(locationId);
        List<Photo> uploadedPhotos = new ArrayList<>();

        List<Photo> photos = storageService.saveLocationPhotos(locationId, files);

        for (Photo photo : photos) {
            Photo savedPhoto = photoRepository.save(photo);
            savedPhoto.setUrl(storageService.getFileUrl(savedPhoto.getFileKey()));
            uploadedPhotos.add(savedPhoto);

            if (location.getPhotoIds() == null) {
                location.setPhotoIds(new ArrayList<>());
            }
            location.getPhotoIds().add(savedPhoto.getId());
        }

        locationRepository.save(location);

        return uploadedPhotos;
    }

    @Transactional(readOnly = true)
    public List<Photo> getLocationPhotos(Integer locationId) {
        Location location = getLocationById(locationId);
        List<Photo> photos = location.getPhotos();

        for (Photo photo : photos) {
            photo.setUrl(storageService.getFileUrl(photo.getFileKey()));
        }

        return photos;
    }

    @Transactional
    public void deleteLocationPhoto(Integer locationId, Integer photoId) {
        Location location = getLocationById(locationId);

        // Удаляем из списка photoIds локации
        if (location.getPhotoIds() != null) {
            location.getPhotoIds().remove(photoId);
            locationRepository.save(location);
        }

        // Если это основное фото, сбрасываем mainPhotoId
        if (location.getMainPhotoId() != null && location.getMainPhotoId().equals(photoId)) {
            location.setMainPhotoId(null);
            locationRepository.save(location);
        }

        // Удаляем фото из БД и облака
        Optional<Photo> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isPresent()) {
            Photo photo = photoOpt.get();
            // Удаляем файл из облачного хранилища
            storageService.deleteFile(photo.getFileKey());
            // Удаляем запись из БД
            photoRepository.deleteById(photoId);
            logger.info("Удалено фото ID: {}, fileKey: {}", photoId, photo.getFileKey());
        }
    }

    @Transactional
    public AdditionalInfo addAdditionalInfo(Integer locationId, AdditionalInfo additionalInfo) {
        Location location = getLocationById(locationId);

        // Сохраняем блок информации
        AdditionalInfo savedInfo = new AdditionalInfo();
        savedInfo.setTitle(additionalInfo.getTitle());
        savedInfo.setDescription(additionalInfo.getDescription());
        savedInfo = additionalInfoRepository.save(savedInfo);

        // Получаем текущий максимальный sortOrder
        List<LocationAdditionalInfo> existingLinks = locationAdditionalInfoRepository
                .findAllByLocationId(locationId);
        int maxSortOrder = existingLinks.stream()
                .mapToInt(LocationAdditionalInfo::getSortOrder)
                .max()
                .orElse(-1);

        // Создаём связь
        LocationAdditionalInfo link = new LocationAdditionalInfo();
        link.setLocationId(locationId);
        link.setAdditionalInfoId(savedInfo.getId());
        link.setSortOrder(maxSortOrder + 1);
        locationAdditionalInfoRepository.save(link);

        return savedInfo;
    }

    @Transactional
    public AdditionalInfo updateAdditionalInfo(Integer locationId, Integer infoId, AdditionalInfo additionalInfo) {
        // Проверяем существование локации
        getLocationById(locationId);

        // Проверяем существование связи
        Optional<LocationAdditionalInfo> linkOpt = locationAdditionalInfoRepository
                .findByLocationIdAndAdditionalInfoId(locationId, infoId);
        if (linkOpt.isEmpty()) {
            throw new RuntimeException("Дополнительная информация с ID " + infoId
                    + " не найдена для локации " + locationId);
        }

        // Обновляем блок информации
        Optional<AdditionalInfo> infoOpt = additionalInfoRepository.findById(infoId);
        if (infoOpt.isEmpty()) {
            throw new RuntimeException("Дополнительная информация с ID " + infoId + " не найдена");
        }

        AdditionalInfo existingInfo = infoOpt.get();
        existingInfo.setTitle(additionalInfo.getTitle());
        existingInfo.setDescription(additionalInfo.getDescription());

        return additionalInfoRepository.save(existingInfo);
    }

    @Transactional
    public void deleteAdditionalInfo(Integer locationId, Integer infoId) {
        // Проверяем существование локации
        getLocationById(locationId);

        // Удаляем связь
        locationAdditionalInfoRepository.deleteByLocationIdAndAdditionalInfoId(locationId, infoId);

        // Проверяем, используется ли этот блок где-то ещё
        boolean isUsedElsewhere = locationAdditionalInfoRepository.existsByAdditionalInfoId(infoId);

        // Если блок нигде не используется, удаляем его
        if (!isUsedElsewhere) {
            additionalInfoRepository.deleteById(infoId);
        }
    }

    @Transactional
    public Point addPointToLocation(Integer locationId, Point point) {
        Location location = getLocationById(locationId);

        // Сохраняем точку
        Point savedPoint = new Point();
        savedPoint.setName(point.getName() != null ? point.getName() : "");
        savedPoint.setDescription(point.getDescription() != null ? point.getDescription() : "");
        savedPoint.setLatitude(point.getLatitude());
        savedPoint.setLongitude(point.getLongitude());
        savedPoint = pointRepository.save(savedPoint);

        // Создаём связь
        LocationPoint link = new LocationPoint();
        link.setLocationId(locationId);
        link.setPointId(savedPoint.getId());
        locationPointRepository.save(link);

        return savedPoint;
    }

    @Transactional
    public Point updatePoint(Integer locationId, Integer pointId, Point point) {
        // Проверяем существование локации
        getLocationById(locationId);

        // Проверяем существование связи
        Optional<LocationPoint> linkOpt = locationPointRepository.findByLocationIdAndPointId(locationId, pointId);
        if (linkOpt.isEmpty()) {
            throw new RuntimeException("Точка с ID " + pointId + " не найдена для локации " + locationId);
        }

        // Обновляем точку
        Optional<Point> pointOpt = pointRepository.findById(pointId);
        if (pointOpt.isEmpty()) {
            throw new RuntimeException("Точка с ID " + pointId + " не найдена");
        }

        Point existingPoint = pointOpt.get();
        existingPoint.setName(point.getName());
        existingPoint.setDescription(point.getDescription());
        existingPoint.setLatitude(point.getLatitude());
        existingPoint.setLongitude(point.getLongitude());

        return pointRepository.save(existingPoint);
    }

    @Transactional
    public void deletePointFromLocation(Integer locationId, Integer pointId) {
        // Проверяем существование локации
        getLocationById(locationId);

        // Удаляем связь
        locationPointRepository.deleteByLocationIdAndPointId(locationId, pointId);

        // Удаляем все фото точки из облака и БД
        List<PointPhoto> pointPhotos = pointPhotoRepository.findAllByPointId(pointId);
        for (PointPhoto pp : pointPhotos) {
            photoRepository.findById(pp.getPhotoId()).ifPresent(photo -> {
                storageService.deleteFile(photo.getFileKey());
                photoRepository.deleteById(photo.getId());
            });
        }
        pointPhotoRepository.deleteByPointId(pointId);

        // Удаляем саму точку
        pointRepository.deleteById(pointId);
    }
}