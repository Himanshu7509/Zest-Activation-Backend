package com.zest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zest.model.Booking;
import com.zest.model.Event;
import com.zest.model.User;
import com.zest.repository.BookingRepository;
import com.zest.repository.EventRepository;
import com.zest.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole().name().equals("USER"))
                .toList();
    }

    @GetMapping("/organizers")
    public List<User> getAllOrganizers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole().name().equals("ORGANIZER"))
                .toList();
    }

    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    
    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @GetMapping("/dashboard")
    public Map<String, Long> getDashboardCounts() {

        long totalUsers = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole().name().equals("USER"))
                .count();

        long totalOrganizers = userRepository.findAll()
                .stream()
                .filter(user -> user.getRole().name().equals("ORGANIZER"))
                .count();

        long totalEvents = eventRepository.count();
        long totalBookings = bookingRepository.count();

        Map<String, Long> dashboard = new HashMap<>();

        dashboard.put("totalUsers", totalUsers);
        dashboard.put("totalOrganizers", totalOrganizers);
        dashboard.put("totalEvents", totalEvents);
        dashboard.put("totalBookings", totalBookings);

        return dashboard;
    }
}
