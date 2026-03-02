package com.zest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(
    name = "spring.cache.type",
    havingValue = "redis",
    matchIfMissing = false
)
@Slf4j
public class CacheManagementService {

    @Autowired(required = false)
    private CacheManager cacheManager;

    public void clearEventCache() {
        if (cacheManager != null) {
            try {
                cacheManager.getCache("events").clear();
                cacheManager.getCache("event").clear();
                log.info("Event caches cleared successfully");
            } catch (Exception e) {
                log.warn("Failed to clear event caches: {}", e.getMessage());
            }
        }
    }

    public void clearUserCache(String email) {
        if (cacheManager != null) {
            try {
                cacheManager.getCache("user").evict(email);
                log.info("User cache cleared for email: {}", email);
            } catch (Exception e) {
                log.warn("Failed to clear user cache for {}: {}", email, e.getMessage());
            }
        }
    }

    public void clearAllCaches() {
        if (cacheManager != null) {
            try {
                cacheManager.getCacheNames().forEach(name -> {
                    cacheManager.getCache(name).clear();
                    log.info("Cache '{}' cleared", name);
                });
                log.info("All caches cleared successfully");
            } catch (Exception e) {
                log.warn("Failed to clear all caches: {}", e.getMessage());
            }
        }
    }
}