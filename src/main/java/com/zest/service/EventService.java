package com.zest.service;

import com.zest.model.Event;
import com.zest.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Event event, String organizerId) {

        event.setEventId("EVT-" + UUID.randomUUID().toString().substring(0,8));
        event.setOrganizerId(organizerId);
        Integer total = event.getTotalSeats();
        event.setAvailableSeats(total != null ? total : 0);
        event.setCreatedAt(LocalDateTime.now());

        return eventRepository.save(event);
    }

    // Get All Events
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // Get Organizer Events
    public List<Event> getEventsByOrganizer(String organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }

    // Delete Event
    public void deleteEvent(String eventId) {
        Event event = eventRepository.findByEventId(eventId);
        if(event != null) {
            eventRepository.delete(event);
        }
    }
}
