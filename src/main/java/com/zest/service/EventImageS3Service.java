package com.zest.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.zest.model.Event;
import com.zest.model.Image;
import com.zest.repository.EventRepository;
import com.zest.repository.ImageRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@ConditionalOnBean(S3Client.class)
@RequiredArgsConstructor
public class EventImageS3Service {

    private final S3Client s3Client;
    private final EventRepository eventRepository;
    private final ImageRepository imageRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private static final String IMAGE_FOLDER = "Zest/events/";

    // ✅ Upload / Replace Event Image
    @Transactional
    public void uploadAndSaveEventImage(
            MultipartFile file,
            String eventId
    ) throws IOException {

        validateFile(file);

        Event event = eventRepository.findByEventId(eventId);

        if (event == null) {
            throw new RuntimeException("Event not found");
        }

        // 🔥 Check for existing image and delete from S3 if exists
        List<Image> existingImages = imageRepository.findByEntityIdAndEntityType(eventId, "EVENT");
        for (Image existingImage : existingImages) {
            if (existingImage.getImageS3Key() != null) {
                s3Client.deleteObject(
                        DeleteObjectRequest.builder()
                                .bucket(bucketName)
                                .key(existingImage.getImageS3Key())
                                .build()
                );
                // Delete the old image record
                imageRepository.deleteById(existingImage.getId());
            }
        }

        // 🔥 Create new S3 key
        String s3Key = IMAGE_FOLDER
                + eventId + "-"
                + UUID.randomUUID()
                + "-" + file.getOriginalFilename();

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                putRequest,
                RequestBody.fromBytes(file.getBytes())
        );

        String imageUrl =
                "https://" + bucketName
                        + ".s3." + s3Client.serviceClientConfiguration().region().id()
                        + ".amazonaws.com/" + s3Key;

        // 🔥 Create and save Image entity
        Image image = Image.builder()
                .entityId(eventId)
                .entityType("EVENT")
                .imageUrl(imageUrl)
                .imageS3Key(s3Key)
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        imageRepository.save(image);
    }

    // ✅ Delete image when event deleted
    @Transactional
    public void deleteEventImage(String eventId) {

        // Find and delete the image record
        List<Image> images = imageRepository.findByEntityIdAndEntityType(eventId, "EVENT");
        
        for (Image image : images) {
            if (image.getImageS3Key() != null) {
                s3Client.deleteObject(
                        DeleteObjectRequest.builder()
                                .bucket(bucketName)
                                .key(image.getImageS3Key())
                                .build()
                );
            }
            // Delete the image record from database
            imageRepository.deleteById(image.getId());
        }
    }

    // ✅ File validation
    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Image file is empty");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("Max file size is 5MB");
        }

        if (!List.of(
                "image/jpeg",
                "image/png",
                "image/webp"
        ).contains(file.getContentType())) {
            throw new RuntimeException("Only JPG, PNG, WEBP allowed");
        }
    }
}
