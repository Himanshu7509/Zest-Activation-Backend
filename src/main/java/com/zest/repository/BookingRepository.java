package com.zest.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.zest.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByUserId(String userId);

    List<Booking> findByEventId(String eventId);

    Optional<Booking> findByUserIdAndEventId(String userId, String eventId);
}
