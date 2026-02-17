package com.zest.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zest.model.Event;
import com.zest.service.EventImageS3Service;
import com.zest.service.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    
    @Autowired(required = false)
    private EventImageS3Service eventImageS3Service;

    // Organizer create event
    @PostMapping("/{organizerId}")
    public Event createEvent(@RequestBody Event event,
                             @PathVariable String organizerId) {
        return eventService.createEvent(event, organizerId);
    }
    
    @PostMapping("/{eventId}/image")
    public ResponseEntity<String> uploadEventImage(
            @PathVariable String eventId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        if (eventImageS3Service == null) {
            return ResponseEntity.status(503).body("Image upload service is not available");
        }
        
        eventImageS3Service.uploadAndSaveEventImage(file, eventId);

        return ResponseEntity.ok("Image uploaded successfully");
    }

    // Public view all events
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    // Organizer view own events
    @GetMapping("/organizer/{organizerId}")
    public List<Event> getOrganizerEvents(@PathVariable String organizerId) {
        return eventService.getEventsByOrganizer(organizerId);
    }

    // Delete event
    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable String eventId) {
        eventService.deleteEvent(eventId);
    }
}
