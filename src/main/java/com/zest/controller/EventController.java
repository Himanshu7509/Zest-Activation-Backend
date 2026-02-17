package com.zest.controller;

import com.zest.model.Event;
import com.zest.service.EventImageS3Service;
import com.zest.service.EventService;


import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private EventImageS3Service eventImageS3Service;

    // Organizer create event
    @PostMapping("/{organizerId}")
    public Event createEvent(@RequestBody Event event,
                             @PathVariable String organizerId) {
        return eventService.createEvent(event, organizerId);
    }
    
    @PostMapping("/events/{eventId}/image")
    public ResponseEntity<String> uploadEventImage(
            @PathVariable String eventId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

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
