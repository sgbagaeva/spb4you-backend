package com.example.spb4you_backend.repositories;

import com.example.spb4you_backend.models.Admin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends CrudRepository<Admin, Integer> {
    // Дополнительные методы для User
    Optional<Admin> findByName(String name);
}
