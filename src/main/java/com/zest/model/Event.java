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
    private String id; // MongoDB internal id

    private String eventId; // Unique readable ID (EVT-XXXXX)

    private String organizerId; // linked to User.userId

    private String title;
    private String description;
    private String category;
    private String location;

    private LocalDateTime eventDate;

    private int totalSeats;
    private int availableSeats;

    private double price;

    private LocalDateTime createdAt;
}
