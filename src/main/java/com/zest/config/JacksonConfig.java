package com.zest.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();

        // ✅ Support Java 8 Date & Time (LocalDate, LocalDateTime)
        mapper.registerModule(new JavaTimeModule());

        // ✅ Disable writing dates as timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // ✅ Set global date format
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // ✅ Ignore null fields in JSON response
    //    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // ✅ Enable pretty print (Optional - remove in production)
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        return mapper;
    }
}