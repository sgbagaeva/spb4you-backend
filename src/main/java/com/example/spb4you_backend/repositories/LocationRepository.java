package com.example.spb4you_backend.repositories;
import com.example.spb4you_backend.models.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends CrudRepository<Location, Integer> {
    // Дополнительные методы для Location, если нужно
    Optional<Location> findByName(String name);
}