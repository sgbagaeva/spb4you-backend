package com.example.spb4you_backend.controllers;

import com.example.spb4you_backend.models.Admin;
import com.example.spb4you_backend.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admins")
public class AdminsController {

    @Autowired
    private AdminService adminService;

    @GetMapping()
    public ResponseEntity<List<Admin>> listAdmins() {
        return ResponseEntity.ok(adminService.findAll());
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<Admin> getAdmin(@PathVariable("adminId") Integer adminId) {
        return adminService.findById(adminId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}


