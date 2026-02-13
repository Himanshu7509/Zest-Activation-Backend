package com.zest.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    private String id;

    private String eventId;

    private String userId;

    private int quantity;

    private LocalDateTime bookingTime;
    
    private BookingStatus status;
}
