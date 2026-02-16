package com.zest.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zest.model.Booking;
import com.zest.model.BookingStatus;
import com.zest.model.Event;
import com.zest.repository.BookingRepository;
import com.zest.repository.EventRepository;

import lombok.RequiredArgsConstructor;

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
                .status(BookingStatus.CONFIRMED)
                .build();

        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(String userId) {
        return bookingRepository.findByUserId(userId);
    }
    
     public Booking cancelBooking(String bookingId, String userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUserId().equals(userId)) {
            throw new RuntimeException("You can only cancel your own booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking already cancelled");
        }

        // Use findByEventId instead of findById since booking.eventId is the custom eventId, not the MongoDB _id
        Event event = eventRepository.findByEventId(booking.getEventId());
        if (event == null) {
            throw new RuntimeException("Event not found with ID: " + booking.getEventId());
        }

        // Restore seats
        event.setAvailableSeats(event.getAvailableSeats() + booking.getQuantity());
        eventRepository.save(event);

        booking.setStatus(BookingStatus.CANCELLED);

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
    return bookingRepository.findAll();
}

}
