package com.zest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    // 1️⃣ Block / Unblock User
    @PutMapping("/users/{id}/block")
    public ResponseEntity<String> toggleBlockUser(@PathVariable String id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(!user.getIsActive());
        userRepository.save(user);

        return ResponseEntity.ok("User block status updated");
    }

    // 2️⃣ Approve Organizer
    @PutMapping("/organizers/{id}/approve")
    public ResponseEntity<String> approveOrganizer(@PathVariable String id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().name().equals("ORGANIZER")) {
            return ResponseEntity.badRequest().body("User is not an organizer");
        }

        user.setApproved(true);
        userRepository.save(user);

        return ResponseEntity.ok("Organizer approved successfully");
    }

    // 3️⃣ Delete Event
    @DeleteMapping("/events/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable String id) {

        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found");
        }

        eventRepository.deleteById(id);

        return ResponseEntity.ok("Event deleted successfully");
    }

    // 4️⃣ Change Event Status
    @PutMapping("/events/{id}/status")
    public ResponseEntity<String> changeEventStatus(
            @PathVariable String id,
            @RequestParam String status) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!status.equals("ACTIVE") && !status.equals("CANCELLED")) {
            return ResponseEntity.badRequest()
                    .body("Status must be ACTIVE or CANCELLED");
        }

        event.setStatus(status);
        eventRepository.save(event);

        return ResponseEntity.ok("Event status updated");
    }

}
