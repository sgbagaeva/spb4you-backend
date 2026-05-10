package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.*;
import com.example.spb4you_backend.models.links.*;
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
public class RouteService extends GenericService<Route, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(RouteService.class);
    private final RouteRepository routeRepository;
    private final PointRepository pointRepository;
    private final PhotoRepository photoRepository;
    private final AdditionalInfoRepository additionalInfoRepository;
    private final RouteAdditionalInfoRepository routeAdditionalInfoRepository;
    private final RoutePointRepository routePointRepository;
    private final PointPhotoRepository pointPhotoRepository;
    private final StorageService storageService;

    public RouteService(RouteRepository routeRepository,
                        PointRepository pointRepository,
                        PhotoRepository photoRepository,
                        AdditionalInfoRepository additionalInfoRepository,
                        RouteAdditionalInfoRepository routeAdditionalInfoRepository,
                        RoutePointRepository routePointRepository,
                        PointPhotoRepository pointPhotoRepository,
                        StorageService storageService) {
        super(routeRepository);
        this.routeRepository = routeRepository;
        this.pointRepository = pointRepository;
        this.photoRepository = photoRepository;
        this.additionalInfoRepository = additionalInfoRepository;
        this.routeAdditionalInfoRepository = routeAdditionalInfoRepository;
        this.routePointRepository = routePointRepository;
        this.pointPhotoRepository = pointPhotoRepository;
        this.storageService = storageService;
    }

    @Transactional(readOnly = true)
    public Route getRouteById(Integer id) {
        Optional<Route> routeOpt = routeRepository.findById(id);
        if (routeOpt.isEmpty()) {
            throw new RuntimeException("Маршрут не найден с ID: " + id);
        }
        Route route = routeOpt.get();
        loadRouteRelations(route);
        // url основной фотографии
        if (route.getMainPhotoId() != null) {
            Optional<Photo> mainPhotoOpt = photoRepository.findById(route.getMainPhotoId());
            if (mainPhotoOpt.isPresent()) {
                String url = storageService.getFileUrl(mainPhotoOpt.get().getFileKey());
                route.setMainPhotoUrl(url);
            }
        }
        return route;
    }

    @Transactional(readOnly = true)
    public List<Route> getAllRoutes() {
        List<Route> routes = (List<Route>) routeRepository.findAll();
        for (Route route : routes) {
            loadRouteRelations(route);
            // url основной фотографии
            if (route.getMainPhotoId() != null) {
                Optional<Photo> mainPhotoOpt = photoRepository.findById(route.getMainPhotoId());
                if (mainPhotoOpt.isPresent()) {
                    String url = storageService.getFileUrl(mainPhotoOpt.get().getFileKey());
                    route.setMainPhotoUrl(url);
                }
            }
        }
        return routes;
    }

    @Transactional(readOnly = true)
    public Photo getMainPhoto(Integer routeId) {
        Route route = getRouteById(routeId);
        if (route.getMainPhotoId() == null) {
            return null;
        }

        Optional<Photo> photoOpt = photoRepository.findById(route.getMainPhotoId());
        if (photoOpt.isPresent()) {
            Photo photo = photoOpt.get();
            try {
                String url = storageService.getFileUrl(photo.getFileKey());
                photo.setUrl(url);
                return photo;
            } catch (Exception e) {
                logger.error("Ошибка при получении URL для основной фотографии {}: {}", photo.getId(), e.getMessage());
                photoRepository.deleteById(photo.getId()); // Удаляем фото, если не удалось получить URL для него
                route.setMainPhotoId(null);
                routeRepository.save(route);
                return null;
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Point getPointById(Integer routeId, Integer pointId) {
        // Проверка существование маршрута
        getRouteById(routeId);

        // Проверка существования связи
        Optional<RoutePoint> linkOpt = routePointRepository.findByRouteIdAndPointId(routeId, pointId);
        if (linkOpt.isEmpty()) {
            throw new RuntimeException("Точка с ID " + pointId + " не найдена в маршруте с ID " + routeId);
        }

        // Получение точки
        Optional<Point> pointOpt = pointRepository.findById(pointId);
        if (pointOpt.isEmpty()) {
            throw new RuntimeException("Точка с ID " + pointId + " не найдена");
        }

        Point point = pointOpt.get();

        // Загрузка фотографий точки
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
                    logger.error("Ошибка при получении URL для фотографии {}: {}", photo.getId(), e.getMessage());
                    photoRepository.deleteById(photo.getId());
                }
            }
        }
        point.setPhotos(pointPhotoList);
        return point;
    }

    // Загрузка связей маршрута
    private void loadRouteRelations(Route route) {
        // Загружаем дополнительную информацию
        List<RouteAdditionalInfo> routeInfo = routeAdditionalInfoRepository
                .findAllByRouteId(route.getId());
        List<AdditionalInfo> additionalInfoList = new ArrayList<>();
        for (RouteAdditionalInfo ri : routeInfo) {
            Optional<AdditionalInfo> infoOpt = additionalInfoRepository.findById(ri.getAdditionalInfoId());
            if (infoOpt.isPresent()) {
                AdditionalInfo info = infoOpt.get();
                additionalInfoList.add(info);
            }
        }
        route.setAdditionalInfo(additionalInfoList);

        // Загрузка точек маршрута
        List<RoutePoint> routePoints = routePointRepository.findAllByRouteId(route.getId());
        List<Point> points = new ArrayList<>();
        for (RoutePoint rp : routePoints) {
            Optional<Point> pointOpt = pointRepository.findById(rp.getPointId());
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
                            logger.error("Ошибка при получении URL для фотографии {}: {}", photo.getId(), e.getMessage());
                            photoRepository.deleteById(photo.getId());
                            logger.warn("Удалена запись о несуществующем фото с ID: {}", photo.getId());
                        }
                    }
                }
                point.setPhotos(pointPhotoList);
                points.add(point);
            }
        }
        route.setPoints(points);
    }

    @Transactional
    public Route createRoute(Route route) {
        logger.info("СОЗДАНИЕ МАРШРУТА");

        if (routeRepository.findByName(route.getName()).isPresent()) {
            throw new RuntimeException("Маршрут с таким именем уже существует");
        }

        Route savedRoute = new Route();
        savedRoute.setName(route.getName());
        savedRoute.setDescription(route.getDescription());
        savedRoute.setCategoryIds(new ArrayList<>());
        savedRoute.setTagIds(new ArrayList<>());
        savedRoute.setLikes(0);
        savedRoute.setDistance(route.getDistance());
        savedRoute.setSteps(route.getSteps());
        savedRoute.setTime(route.getTime());

        savedRoute = routeRepository.save(savedRoute);
        logger.info("Создан маршрут с ID: {}", savedRoute.getId());

        // Сохранение дополнительной информации
        if (route.getAdditionalInfo() != null && !route.getAdditionalInfo().isEmpty()) {
            for (int i = 0; i < route.getAdditionalInfo().size(); i++) {
                AdditionalInfo info = route.getAdditionalInfo().get(i);

                AdditionalInfo savedInfo = new AdditionalInfo();
                savedInfo.setTitle(info.getTitle());
                savedInfo.setDescription(info.getDescription() != null ? info.getDescription() : "");
                savedInfo = additionalInfoRepository.save(savedInfo);

                RouteAdditionalInfo link = new RouteAdditionalInfo();
                link.setRouteId(savedRoute.getId());
                link.setAdditionalInfoId(savedInfo.getId());
                link.setSortOrder(i);
                routeAdditionalInfoRepository.save(link);
            }
        }

        // Сохранение точки
        if (route.getPoints() != null && !route.getPoints().isEmpty()) {
            for (int i = 0; i < route.getPoints().size(); i++) {
                Point point = route.getPoints().get(i);

                Point savedPoint = new Point();
                savedPoint.setName(point.getName());
                savedPoint.setDescription(point.getDescription() != null ? point.getDescription() : "");
                savedPoint.setLatitude(point.getLatitude());
                savedPoint.setLongitude(point.getLongitude());
                savedPoint = pointRepository.save(savedPoint);

                RoutePoint link = new RoutePoint();
                link.setRouteId(savedRoute.getId());
                link.setPointId(savedPoint.getId());
                routePointRepository.save(link);

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
        return getRouteById(savedRoute.getId());
    }

    @Transactional
    public Route updateRoute(Integer id, Route route) {
        logger.info("ОБНОВЛЕНИЕ МАРШРУТА ID: {}", id);

        Optional<Route> routeOpt = routeRepository.findById(id);
        if (routeOpt.isEmpty()) {
            throw new RuntimeException("Маршрут с ID " + id + " не найден");
        }
        Route existingRoute = routeOpt.get();

        existingRoute.setName(route.getName());
        existingRoute.setDescription(route.getDescription());
        existingRoute.setDistance(route.getDistance());
        existingRoute.setTime(route.getTime());
        existingRoute.setSteps(route.getSteps());

        // Обновление основной фотографии, если она была изменена
        if (route.getMainPhotoId() != null) {
            existingRoute.setMainPhotoId(route.getMainPhotoId());
        }

        // Обновление categoryIds, если они были изменены
        if (route.getCategoryIds() != null) {
            existingRoute.setCategoryIds(route.getCategoryIds());
        }

        // Обновление tagIds, если они были изменены
        if (route.getTagIds() != null) {
            existingRoute.setTagIds(route.getTagIds());
        }

        routeRepository.save(existingRoute);

        // Обновление дополнительной информации
        if (route.getAdditionalInfo() != null) {
        routeAdditionalInfoRepository.deleteByRouteId(id);

        if (!route.getAdditionalInfo().isEmpty()) {
            for (int i = 0; i < route.getAdditionalInfo().size(); i++) {
                AdditionalInfo info = route.getAdditionalInfo().get(i);

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

                RouteAdditionalInfo link = new RouteAdditionalInfo();
                link.setRouteId(id);
                link.setAdditionalInfoId(savedInfo.getId());
                link.setSortOrder(i);
                routeAdditionalInfoRepository.save(link);
            }
        }
    }

    // Обновляем точки только если они переданы
        if (route.getPoints() != null) {
        updatePoints(id, route.getPoints());
    }

        return getRouteById(id);
}

    private void updatePoints(Integer routeId, List<Point> newPoints) {
        List<RoutePoint> existingLinks = routePointRepository.findAllByRouteId(routeId);

        // Если newPoints == null, ничего не делаем
        if (newPoints == null) {
            return;
        }

        // Получаем ID существующих точек
        List<Integer> existingPointIds = existingLinks.stream()
                .map(RoutePoint::getPointId)
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
            boolean linkExists = routePointRepository.existsByRouteIdAndPointId(routeId, savedPoint.getId());
            if (!linkExists) {
                RoutePoint link = new RoutePoint();
                link.setRouteId(routeId);
                link.setPointId(savedPoint.getId());
                routePointRepository.save(link);
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
    public void deleteRoute(Integer id) {
        logger.info("НАЧАЛО УДАЛЕНИЯ МАРШРУТА ID: {}", id);

        try {
            // 1. Получаем маршрут со всеми данными
            Route route = getRouteById(id);
            if (route == null) {
                logger.error("Маршрут с ID {} не найден", id);
                throw new RuntimeException("Маршрут не найден");
            }

            // 2. Сохраняем списки ID для последующего удаления
            List<Integer> photoIdsToDelete = new ArrayList<>();
            if (route.getMainPhotoId() != null && !photoIdsToDelete.contains(route.getMainPhotoId())) {
                photoIdsToDelete.add(route.getMainPhotoId());
            }

            // 3. Получаем все точки маршрута и их фото
            List<RoutePoint> routePoints = routePointRepository.findAllByRouteId(id);
            List<Integer> pointIds = new ArrayList<>();
            List<Integer> pointPhotoIdsToDelete = new ArrayList<>();

            for (RoutePoint rp : routePoints) {
                pointIds.add(rp.getPointId());
                List<PointPhoto> pointPhotos = pointPhotoRepository.findAllByPointId(rp.getPointId());
                for (PointPhoto pp : pointPhotos) {
                    pointPhotoIdsToDelete.add(pp.getPhotoId());
                }
            }

            // 4. Сохраняем ID дополнительной информации ДО удаления связей
            List<RouteAdditionalInfo> additInfoList = routeAdditionalInfoRepository
                    .findAllByRouteId(id);
            List<Integer> additionalInfoIdsToDelete = new ArrayList<>();
            for (RouteAdditionalInfo ri : additInfoList) {
                additionalInfoIdsToDelete.add(ri.getAdditionalInfoId());
            }
            logger.info("Найдено {} блоков дополнительной информации для удаления", additionalInfoIdsToDelete.size());

            // 5. ОЧЕНЬ ВАЖНО: Сначала убираем ссылки на фото из таблицы routes
            route.setMainPhotoId(null);
            routeRepository.save(route);
            logger.info("Очищены ссылки на фото в маршруте с ID: {}", id);

            // 6. Удаляем PointPhoto связи (фото точек)
            for (Integer pointId : pointIds) {
                pointPhotoRepository.deleteByPointId(pointId);
                logger.info("Удалены PointPhoto связи для точки ID: {}", pointId);
            }

            // 7. Удаляем RoutePoint связи
            routePointRepository.deleteByRouteId(id);
            logger.info("Удалены RoutePoint связи для маршрута ID: {}", id);

            // 8. Удаляем RouteAdditionalInfo связи
            routeAdditionalInfoRepository.deleteByRouteId(id);
            logger.info("Удалены RouteAdditionalInfo связи для маршрута ID: {}", id);

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

            // 10. Удаляем общие фото маршрута из БД и облака
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

            // 13. Удаляем маршрут
            routeRepository.deleteById(id);
            logger.info("Маршрут с ID: {} удален", id);

            logger.info("МАРШРУТ С ID: {} УСПЕШНО УДАЛЕН", id);
            logger.info("Удалено фото точек: {}", pointPhotoIdsToDelete.size());
            logger.info("Удалено общих фото: {}", photoIdsToDelete.size());
            logger.info("Удалено точек: {}", pointIds.size());
            logger.info("Удалено блоков дополнительной информации: {}", additionalInfoIdsToDelete.size());

        } catch (Exception e) {
            logger.error("Ошибка при удалении маршрута {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Не удалось удалить маршрут: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Photo uploadMainLocationPhoto(Integer routeId, MultipartFile file) {
        Route route = getRouteById(routeId);

        Photo photo = storageService.saveLocationMainPhoto(routeId, file);
        if (photo == null) {
            throw new RuntimeException("Не удалось загрузить фото");
        }

        photo = photoRepository.save(photo);
        photo.setUrl(storageService.getFileUrl(photo.getFileKey()));

        route.setMainPhotoId(photo.getId());
        routeRepository.save(route);

        return photo;
    }

    @Transactional
    public AdditionalInfo addAdditionalInfo(Integer routeId, AdditionalInfo additionalInfo) {
        Route route = getRouteById(routeId);

        // Сохраняем блок информации
        AdditionalInfo savedInfo = new AdditionalInfo();
        savedInfo.setTitle(additionalInfo.getTitle());
        savedInfo.setDescription(additionalInfo.getDescription());
        savedInfo = additionalInfoRepository.save(savedInfo);

        // Получаем текущий максимальный sortOrder
        List<RouteAdditionalInfo> existingLinks = routeAdditionalInfoRepository
                .findAllByRouteId(routeId);
        int maxSortOrder = existingLinks.stream()
                .mapToInt(RouteAdditionalInfo::getSortOrder)
                .max()
                .orElse(-1);

        // Создаём связь
        RouteAdditionalInfo link = new RouteAdditionalInfo();
        link.setRouteId(routeId);
        link.setAdditionalInfoId(savedInfo.getId());
        link.setSortOrder(maxSortOrder + 1);
        routeAdditionalInfoRepository.save(link);

        return savedInfo;
    }

    @Transactional
    public AdditionalInfo updateAdditionalInfo(Integer routeId, Integer infoId, AdditionalInfo additionalInfo) {
        // Проверяем существование маршрута
        getRouteById(routeId);

        // Проверяем существование связи
        Optional<RouteAdditionalInfo> linkOpt = routeAdditionalInfoRepository
                .findByRouteIdAndAdditionalInfoId(routeId, infoId);
        if (linkOpt.isEmpty()) {
            throw new RuntimeException("Дополнительная информация с ID " + infoId
                    + " не найдена для маршрута " + routeId);
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
    public void deleteAdditionalInfo(Integer routeId, Integer infoId) {
        // Проверяем существование маршрута
        getRouteById(routeId);

        // Удаляем связь
        routeAdditionalInfoRepository.deleteByRouteIdAndAdditionalInfoId(routeId, infoId);

        // Проверяем, используется ли этот блок где-то ещё
        boolean isUsedElsewhere = routeAdditionalInfoRepository.existsByAdditionalInfoId(infoId);

        // Если блок нигде не используется, удаляем его
        if (!isUsedElsewhere) {
            additionalInfoRepository.deleteById(infoId);
        }
    }

    @Transactional
    public Point addPointToRoute(Integer routeId, Point point) {
        Route route = getRouteById(routeId);

        // Сохраняем точку
        Point savedPoint = new Point();
        savedPoint.setName(point.getName());
        savedPoint.setDescription(point.getDescription() != null ? point.getDescription() : "");
        savedPoint.setLatitude(point.getLatitude());
        savedPoint.setLongitude(point.getLongitude());
        savedPoint = pointRepository.save(savedPoint);

        // Создаём связь
        RoutePoint link = new RoutePoint();
        link.setRouteId(routeId);
        link.setPointId(savedPoint.getId());
        routePointRepository.save(link);

        return savedPoint;
    }

    @Transactional
    public Point updatePoint(Integer routeId, Integer pointId, Point point) {
        // Проверяем существование маршрута
        getRouteById(routeId);

        // Проверяем существование связи
        Optional<RoutePoint> linkOpt = routePointRepository.findByRouteIdAndPointId(routeId, pointId);
        if (linkOpt.isEmpty()) {
            throw new RuntimeException("Точка с ID " + pointId + " не найдена для маршрута " + routeId);
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
    public void deletePointFromRoute(Integer routeId, Integer pointId) {
        // Проверяем существование маршрута
        getRouteById(routeId);

        // Удаляем связь
        routePointRepository.deleteByRouteIdAndPointId(routeId, pointId);

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
