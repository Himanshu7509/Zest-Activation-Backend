package com.zest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zest.model.Event;
import com.zest.model.Image;
import com.zest.model.Role;
import com.zest.model.User;
import com.zest.repository.EventRepository;
import com.zest.repository.ImageRepository;
import com.zest.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    
    // Optional dependency - may be null if S3 is not configured
    @Autowired(required = false)
    private EventImageS3Service eventImageS3Service;

    public Event createEvent(Event event, String organizerId) {

    User organizer = userRepository.findById(organizerId)
            .orElseThrow(() -> new RuntimeException("Organizer not found"));

    if (organizer.getRole() != Role.ORGANIZER) {
        throw new RuntimeException("Only organizers can create events");
    }

    if (Boolean.FALSE.equals(organizer.getIsActive())) {
        throw new RuntimeException("Organizer account is blocked");
    }

    event.setEventId("EVT-" + UUID.randomUUID().toString().substring(0, 8));
    event.setOrganizerId(organizerId);

    Integer total = event.getTotalSeats();
    event.setAvailableSeats(total != null ? total : 0);

    event.setStatus("PENDING");   // 🔥 important
    event.setIsDeleted(false);
    event.setCreatedAt(LocalDateTime.now());

    return eventRepository.save(event);
}

    // Get All Events
    public List<Event> getAllEvents() {

    return eventRepository.findAll()
            .stream()
            .filter(event -> 
                Boolean.FALSE.equals(event.getIsDeleted()))
            .toList();
}
    // Get Organizer Events
    public List<Event> getEventsByOrganizer(String organizerId) {

    return eventRepository.findByOrganizerId(organizerId)
            .stream()
            .filter(event -> 
                Boolean.FALSE.equals(event.getIsDeleted()))
            .toList();
}

    // Delete Event
    public void deleteEvent(String eventId) {

        Event event = eventRepository.findByEventId(eventId);

        if (event == null) {
            throw new RuntimeException("Event not found");
        }

        // 🔥 Delete S3 image first if S3 service is available
        if (eventImageS3Service != null) {
            eventImageS3Service.deleteEventImage(eventId);
        }

        // Soft delete
        event.setIsDeleted(true);
        eventRepository.save(event);
    }

    // Method to get event with image information from separate collection
    public Event getEventWithImage(String eventId) {
        Event event = eventRepository.findByEventId(eventId);
        if (event != null) {
            // Get image information from the separate Image collection
            List<Image> images = imageRepository.findByEntityIdAndEntityType(eventId, "EVENT");
            if (!images.isEmpty()) {
                Image image = images.get(0); // Get the first image
                event.setImageUrl(image.getImageUrl());
                event.setImageS3Key(image.getImageS3Key());
            }
        }
        return event;
    }

    // Method to get all events with their image information
    public List<Event> getAllEventsWithImages() {
        List<Event> events = getAllEvents();
        
        for (Event event : events) {
            List<Image> images = imageRepository.findByEntityIdAndEntityType(event.getEventId(), "EVENT");
            if (!images.isEmpty()) {
                Image image = images.get(0); // Get the first image
                event.setImageUrl(image.getImageUrl());
                event.setImageS3Key(image.getImageS3Key());
            }
        }
        
        return events;
    }

}
