package com.zest.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.zest.model.Event;
import com.zest.model.Role;
import com.zest.model.User;
import com.zest.repository.EventRepository;
import com.zest.repository.UserRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    
    // Optional dependency - may be null if S3 is not configured
    @Autowired(required = false)
    private EventImageS3Service eventImageS3Service;
    
    public EventService(EventRepository eventRepository, UserRepository userRepository, EmailService emailService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

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

    event.setStatus("PENDING");
    event.setIsDeleted(false);
    event.setCreatedAt(LocalDateTime.now());

    return eventRepository.save(event);
}

    // Get All Events
    @Cacheable(value = "events", key = "#root.methodName")
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

    // Get Event by ID
    @Cacheable(value = "event", key = "#eventId")
    public Event getEventById(String eventId) {
        Event event = eventRepository.findByEventId(eventId);
        
        if (event == null || Boolean.TRUE.equals(event.getIsDeleted())) {
            throw new RuntimeException("Event not found");
        }
        
        return event;
    }

    // Delete Event
    @CacheEvict(value = {"events", "event"}, allEntries = true)
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

    // Update Event
    @CacheEvict(value = {"events", "event"}, allEntries = true)
    public Event updateEvent(String eventId, Event updatedEvent) {
        Event existingEvent = eventRepository.findByEventId(eventId);
        
        if (existingEvent == null) {
            throw new RuntimeException("Event not found");
        }
        
        // Update fields that are allowed to be modified
        if (updatedEvent.getTitle() != null) {
            existingEvent.setTitle(updatedEvent.getTitle());
        }
        if (updatedEvent.getDescription() != null) {
            existingEvent.setDescription(updatedEvent.getDescription());
        }
        if (updatedEvent.getLocation() != null) {
            existingEvent.setLocation(updatedEvent.getLocation());
        }
        if (updatedEvent.getEventDate() != null) {
            existingEvent.setEventDate(updatedEvent.getEventDate());
        }
        if (updatedEvent.getTotalSeats() != null) {
            existingEvent.setTotalSeats(updatedEvent.getTotalSeats());
            existingEvent.setAvailableSeats(updatedEvent.getTotalSeats());
        }
        if (updatedEvent.getPrice() != null) {
            existingEvent.setPrice(updatedEvent.getPrice());
        }
        
        existingEvent.setCreatedAt(LocalDateTime.now());
        return eventRepository.save(existingEvent);
    }



}
