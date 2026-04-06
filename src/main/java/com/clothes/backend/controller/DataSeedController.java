package com.clothes.backend.controller;

import com.clothes.backend.service.DataSeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class DataSeedController {

    private final DataSeedService dataSeedService;

    @PostMapping("/reseed-database")
    public ResponseEntity<?> reseedDatabase() {
        try {
            dataSeedService.reseedDatabase();
            return ResponseEntity.ok(Map.of(
                "message", "Database reseeded successfully with clean, linked data!",
                "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "Error during reseed: " + e.getMessage(),
                "status", "error"
            ));
        }
    }
}
