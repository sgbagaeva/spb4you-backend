package com.example.spb4you_backend;
import com.example.spb4you_backend.models.Location;
import com.example.spb4you_backend.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Main implements CommandLineRunner {

    @Autowired
    private LocationService locationService;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Приложение запустилось");
    }
}
