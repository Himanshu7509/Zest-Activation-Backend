package com.zest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cache.type=redis",
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
class RedisCacheTest {

    @Test
    void contextLoads() {
        // This test will verify that the application context loads correctly with Redis configuration
    }
}