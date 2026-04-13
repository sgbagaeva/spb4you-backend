package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.Category;
import com.example.spb4you_backend.models.Location;
import com.example.spb4you_backend.models.Route;
import com.example.spb4you_backend.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService extends GenericService<Category, Integer> {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
    }

    /**
     * Поиск категории по имени
     * @param name
     * @return
     */
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Autowired
    private LocationService locationService;

    @Autowired
    private RouteService routeService;

    /**
     * Создание категории
     * @param name
     * @param description
     * @param type
     * @return
     */
    @Transactional
    public Category createCategory(String name, String description, String type) {
        Category category = new Category(name, description, type);
        return categoryRepository.save(category);
    }

    /**
     * Обновление категории
     * @param id
     * @param name
     * @param description
     * @param type
     * @return
     */
    @Transactional
    public Category updateCategory(Integer id, String name, String description, String type) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);

        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("Категория с ID " + id + " не найдена");
        }
        Category category = categoryOpt.get();

        category.setName(name);
        category.setDescription(description);
        category.setType(type);

        return categoryRepository.save(category);
    }

    /**
     * Обновление связей для локаций
     * @param categoryId ID категории
     * @param locationIds Список ID локаций, которые должны принадлежать категории
     */
    @Transactional
    public void updateLocationRelationships(Integer categoryId, List<Integer> locationIds) {
        // 1. Получаем все локации
        List<Location> allLocations = locationService.findAll();

        // 2. Для каждой локации обновляем список категорий
        for (Location location : allLocations) {
            List<Integer> currentCategoryIds = location.getCategoryIds();

            // Проверяем, должна ли локация принадлежать категории
            boolean shouldBeInCategory = locationIds.contains(location.getId());
            boolean isCurrentlyInCategory = currentCategoryIds.contains(categoryId);

            if (shouldBeInCategory && !isCurrentlyInCategory) {
                // Добавляем категорию
                currentCategoryIds.add(categoryId);
                location.setCategoryIds(currentCategoryIds);
                locationService.save(location);
            } else if (!shouldBeInCategory && isCurrentlyInCategory) {
                // Удаляем категорию
                currentCategoryIds.remove(categoryId);
                location.setCategoryIds(currentCategoryIds);
                locationService.save(location);
            }
        }
    }

    /**
     * Обновление связей для маршрутов
     * @param categoryId ID категории
     * @param routeIds Список ID маршрутов, которые должны принадлежать категории
     */
    @Transactional
    public void updateRouteRelationships(Integer categoryId, List<Integer> routeIds) {
        List<Route> allRoutes = routeService.findAll();

        for (Route route : allRoutes) {
            List<Integer> currentCategoryIds = route.getCategoryIds();

            boolean shouldBeInCategory = routeIds.contains(route.getId());
            boolean isCurrentlyInCategory = currentCategoryIds.contains(categoryId);

            if (shouldBeInCategory && !isCurrentlyInCategory) {
                currentCategoryIds.add(categoryId);
                route.setCategoryIds(currentCategoryIds);
                routeService.save(route);
            } else if (!shouldBeInCategory && isCurrentlyInCategory) {
                currentCategoryIds.remove(categoryId);
                route.setCategoryIds(currentCategoryIds);
                routeService.save(route);
            }
        }
    }

    /**
     * Удаление категории и её ID из всех связанных элементов
     * @param categoryId ID удаляемой категории
     */
    @Transactional
    public void deleteCategory(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Категория с ID " + categoryId + " не найдена");
        }
        // Удаляем категорию из всех локаций
        List<Location> allLocations = locationService.findAll();
        for (Location location : allLocations) {
            List<Integer> categoryIds = location.getCategoryIds();
            if (categoryIds.contains(categoryId)) {
                categoryIds.remove(categoryId);
                location.setCategoryIds(categoryIds);
                locationService.save(location);
            }
        }

        // Удаляем категорию из всех маршрутов
        List<Route> allRoutes = routeService.findAll();
        for (Route route : allRoutes) {
            List<Integer> categoryIds = route.getCategoryIds();
            if (categoryIds.contains(categoryId)) {
                categoryIds.remove(categoryId);
                route.setCategoryIds(categoryIds);
                routeService.save(route);
            }
        }
        categoryRepository.deleteById(categoryId);
    }
}
