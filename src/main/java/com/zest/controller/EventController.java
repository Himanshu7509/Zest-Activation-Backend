package com.zest.controller;

import com.zest.model.Event;
import com.zest.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // Organizer create event
    @PostMapping("/{organizerId}")
    public Event createEvent(@RequestBody Event event,
                             @PathVariable String organizerId) {
        return eventService.createEvent(event, organizerId);
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
