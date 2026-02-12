package com.zest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.zest.model.Booking;
import com.zest.service.BookingService;

import java.util.List;

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
}
