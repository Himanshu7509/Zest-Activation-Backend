package com.zest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zest.model.Booking;
import com.zest.service.BookingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // USER books event
    @PostMapping("/{userId}/{eventId}")
    public Booking bookEvent(
            @PathVariable String userId,
            @PathVariable String eventId,
            @RequestParam Integer quantity
    ) {
        return bookingService.bookEvent(userId, eventId, quantity);
    }

    // USER sees own bookings
    @GetMapping("/user/{userId}")
    public List<Booking> getUserBookings(@PathVariable String userId) {
        return bookingService.getUserBookings(userId);
    }
    
    @PutMapping("/cancel/{bookingId}/{userId}")
    public Booking cancelBooking(
            @PathVariable String bookingId,
            @PathVariable String userId
    ) {
        return bookingService.cancelBooking(bookingId, userId);
    }

    @GetMapping("/all")
    public List<Booking> getAllBookings() {
    return bookingService.getAllBookings();
}

}
