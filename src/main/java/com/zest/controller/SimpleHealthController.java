package com.zest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class SimpleHealthController {

    @GetMapping
    public ResponseEntity<Map<String, String>> getHealth() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "Application is running"
        ));
    }
}
