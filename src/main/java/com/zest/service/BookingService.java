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

    public Booking bookEvent(String userId, String eventId, int quantity) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getAvailableSeats() < quantity) {
            throw new RuntimeException("Not enough seats available");
        }

        event.setAvailableSeats(event.getAvailableSeats() - quantity);
        eventRepository.save(event);

        Booking booking = Booking.builder()
                .eventId(eventId)
                .userId(userId)
                .quantity(quantity)
                .bookingTime(LocalDateTime.now())
                .build();

        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(String userId) {
        return bookingRepository.findByUserId(userId);
    }
}
