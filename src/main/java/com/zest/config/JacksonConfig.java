package com.zest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import jakarta.annotation.PostConstruct;

@Configuration
public class JacksonConfig {

    private final Jackson2ObjectMapperBuilder builder;

    public JacksonConfig(Jackson2ObjectMapperBuilder builder) {
        this.builder = builder;
    }

    @PostConstruct
    public void configure() {
        builder.featuresToDisable(
            com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        );
    }
}
