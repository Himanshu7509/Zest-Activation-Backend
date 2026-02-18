package com.zest.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.zest.model.Image;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {
    
    List<Image> findByEntityId(String entityId);
    
    List<Image> findByEntityIdAndEntityType(String entityId, String entityType);
    
   // Optional<Image> findByEntityIdAndEntityType(String entityId, String entityType);
    
    void deleteByEntityIdAndEntityType(String entityId, String entityType);
}