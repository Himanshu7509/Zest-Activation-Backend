package com.zest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.zest.model.*;
import com.zest.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    public Booking bookEvent(String userId, String eventId, Integer quantity) {

        Event event = eventRepository.findByEventId(eventId);
        if (event == null) {
            throw new RuntimeException("Event not found");
        }

        int qty = quantity != null ? quantity : 1;
        
        if (event.getAvailableSeats() == null || event.getAvailableSeats() < qty) {
            throw new RuntimeException("Not enough seats available");
        }

        event.setAvailableSeats(event.getAvailableSeats() - qty);
        eventRepository.save(event);

        Booking booking = Booking.builder()
                .eventId(eventId)
                .userId(userId)
                .quantity(qty)
                .bookingTime(LocalDateTime.now())
                .build();

        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(String userId) {
        return bookingRepository.findByUserId(userId);
    }
}
