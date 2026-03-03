package com.example.spb4you_backend.services;

import com.example.spb4you_backend.models.Admin;
import com.example.spb4you_backend.repositories.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService extends GenericService<Admin, Integer> {
    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        super(adminRepository);
        this.adminRepository = adminRepository;
    }

    // Метод для поиска пользователя по имени
    public Optional<Admin> findByName(String name) {
        return adminRepository.findByName(name);
    }
}

