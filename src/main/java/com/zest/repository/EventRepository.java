package com.zest.repository;

import com.zest.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {

    List<Event> findByOrganizerId(String organizerId);

    Event findByEventId(String eventId);
}
