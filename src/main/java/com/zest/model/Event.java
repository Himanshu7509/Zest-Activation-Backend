package com.zest.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    private String id; 

    private String eventId; 

    private String organizerId; 

    private String title;
    private String description;
    private String category;
    private String location;

    private LocalDateTime eventDate;

    private Integer totalSeats;
    private Integer availableSeats;

    private Double price;

     private String status = "PENDING";

    private Boolean isDeleted = false;
    
    // Note: Image information is now stored separately in Image collection
    // These fields are kept for backward compatibility
    private String imageUrl;
    
    private String imageS3Key;

    private LocalDateTime createdAt;
}
