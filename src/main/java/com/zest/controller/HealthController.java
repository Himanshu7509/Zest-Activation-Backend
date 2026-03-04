
package com.zest.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/redis")
    public ResponseEntity<?> checkRedisConnection() {
        try {
            log.info("Checking Redis connection...");
            redisTemplate.opsForValue().set("health:check", "test");
            String value = (String) redisTemplate.opsForValue().get("health:check");
            redisTemplate.delete("health:check");
            
            if ("test".equals(value)) {
                log.info("Redis connection successful");
                return ResponseEntity.ok().body(Map.of(
                    "status", "connected",
                    "message", "Redis is working properly"
                ));
            } else {
                log.error("Redis read/write test failed");
                return ResponseEntity.status(503).body(Map.of(
                    "status", "error",
                    "message", "Redis read/write test failed"
                ));
            }
        } catch (Exception e) {
            log.error("Redis connection check failed: {}", e.getMessage(), e);
            return ResponseEntity.status(503).body(Map.of(
                "status", "error",
                "message", "Unable to connect to Redis: " + e.getMessage()
            ));
        }
    }
}
