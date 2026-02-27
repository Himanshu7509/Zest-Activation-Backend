package com.zest.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.zest.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;

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

    return eventRepository.findAll()
            .stream()
            .filter(event -> 
                "ACTIVE".equals(event.getStatus()) &&
                Boolean.FALSE.equals(event.getIsDeleted()))
            .toList();
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

   @PutMapping("/events/{eventId}/approve")
   public ResponseEntity<String> approveEvent(@PathVariable String eventId) {

    Event event = eventRepository.findByEventId(eventId);

    if (event == null) {
        throw new RuntimeException("Event not found");
    }

    event.setStatus("ACTIVE");
    eventRepository.save(event);
    
    // Get the organizer to send email notification
    User organizer = userRepository.findById(event.getOrganizerId()).orElse(null);
    if (organizer != null) {
        log.info("Triggering event approval notification email for: {}", organizer.getEmail());
        boolean emailSent = emailService.sendEventApprovalNotification(organizer.getEmail(), event.getTitle(), event.getEventId());
        if (emailSent) {
            log.info("Event approval notification sent successfully to: {}", organizer.getEmail());
        } else {
            log.warn("Failed to send event approval notification to: {}", organizer.getEmail());
        }
    }

    return ResponseEntity.ok("Event approved successfully");
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

    @PutMapping("/events/{eventId}/reject")
   public ResponseEntity<String> rejectEvent(@PathVariable String eventId) {

    Event event = eventRepository.findByEventId(eventId);

    if (event == null) {
        throw new RuntimeException("Event not found");
    }

    event.setStatus("REJECTED");
    eventRepository.save(event);
    
    // Get the organizer to send email notification
    User organizer = userRepository.findById(event.getOrganizerId()).orElse(null);
    if (organizer != null) {
        log.info("Triggering event rejection notification email for: {}", organizer.getEmail());
        boolean emailSent = emailService.sendEventRejectionNotification(organizer.getEmail(), event.getTitle(), event.getEventId());
        if (emailSent) {
            log.info("Event rejection notification sent successfully to: {}", organizer.getEmail());
        } else {
            log.warn("Failed to send event rejection notification to: {}", organizer.getEmail());
        }
    }

    return ResponseEntity.ok("Event rejected successfully");
}


}
