package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.Location;
import com.example.spb4you_backend.models.Route;
import com.example.spb4you_backend.models.Tag;
import com.example.spb4you_backend.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TagService extends GenericService<Tag, Integer> {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        super(tagRepository);
        this.tagRepository = tagRepository;
    }

    /**
     * Поиск тега по названию (имени)
     * @param name
     * @return
     */
    public Optional<Tag> findByName(String name) {
        return tagRepository.findByName(name);
    }

    @Autowired
    private LocationService locationService;

    @Autowired
    private RouteService routeService;

    /**
     * Создание тега
     * @param name
     * @param color
     * @return
     */
    @Transactional
    public Tag createTag(String name, String color) {
        Tag tag = new Tag(name, color);
        return tagRepository.save(tag);
    }

    /**
     * Обновление тега
     * @param id
     * @param name
     * @param color
     * @return
     */
    @Transactional
    public Tag updateTag(Integer id, String name, String color) {
        Optional<Tag> tagOpt = tagRepository.findById(id);

        if (tagOpt.isEmpty()) {
            throw new RuntimeException("Тег с ID " + id + " не найден");
        }

        Tag tag = tagOpt.get();
        tag.setName(name);
        tag.setColor(color);

        return tagRepository.save(tag);
    }

    /**
     * Обновление связей для тега
     * @param tagId ID тега
     * @param locationIds Список ID локаций, которые должны принадлежать тегу
     */
    @Transactional
    public void updateLocationRelationships(Integer tagId, List<Integer> locationIds) {
        List<Location> allLocations = locationService.findAll();

        for (Location location : allLocations) {
            List<Integer> currentTagIds = location.getTagIds();

            // Проверяем, должна ли локация принадлежать тегу
            boolean shouldBeInTag = locationIds.contains(location.getId());
            boolean isCurrentlyInTag = currentTagIds.contains(tagId);

            if (shouldBeInTag && !isCurrentlyInTag) {
                // Добавляем тег
                currentTagIds.add(tagId);
                location.setTagIds(currentTagIds);
                locationService.save(location);
            } else if (!shouldBeInTag && isCurrentlyInTag) {
                // Удаляем тег
                currentTagIds.remove(tagId);
                location.setTagIds(currentTagIds);
                locationService.save(location);
            }
        }
    }

    /**
     * Обновление связей для тега
     * @param tagId ID тега
     * @param routeIds Список ID маршрутов, которые должны принадлежать тегу
     */
    @Transactional
    public void updateRouteRelationships(Integer tagId, List<Integer> routeIds) {
        List<Route> allRoutes = routeService.findAll();

        for (Route route : allRoutes) {
            List<Integer> currentTagIds = route.getTagIds();

            boolean shouldBeInTag = routeIds.contains(route.getId());
            boolean isCurrentlyInTag = currentTagIds.contains(tagId);

            if (shouldBeInTag && !isCurrentlyInTag) {
                // Добавляем тег
                currentTagIds.add(tagId);
                route.setTagIds(currentTagIds);
                routeService.save(route);
            } else if (!shouldBeInTag && isCurrentlyInTag) {
                // Удаляем тег
                currentTagIds.remove(tagId);
                route.setTagIds(currentTagIds);
                routeService.save(route);
            }
        }
    }

    /**
     * Удаление тега и его ID из всех связанных элементов
     * @param tagId ID удаляемой категории
     */
    @Transactional
    public void deleteTag(Integer tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new IllegalArgumentException("Тег с ID " + tagId + " не найден");
        }

        // Удаление тега из всех локаций
        List<Location> locations = locationService.findAll();
        for (Location location : locations) {
            List<Integer> tagIds = location.getTagIds();
            if (tagIds.contains(tagId)) {
                tagIds.remove(tagId);
                location.setTagIds(tagIds);
                locationService.save(location);
            }
        }

        // Удаление тега из всех маршрутов
        List<Route> routes = routeService.findAll();
        for (Route route : routes) {
            List<Integer> tagIds = route.getTagIds();
            if (tagIds.contains(tagId)) {
                tagIds.remove(tagId);
                route.setTagIds(tagIds);
                routeService.save(route);
            }
        }
        tagRepository.deleteById(tagId);
    }
}
