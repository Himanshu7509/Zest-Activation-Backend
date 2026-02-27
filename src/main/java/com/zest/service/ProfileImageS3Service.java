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

import com.zest.model.User;
import com.zest.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@ConditionalOnBean(S3Client.class)
@RequiredArgsConstructor
public class ProfileImageS3Service {

    private final S3Client s3Client;
    private final UserRepository userRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private static final String IMAGE_FOLDER = "Zest/profiles/";

    // ✅ Upload / Replace Profile Image
    @Transactional
    public void uploadAndSaveProfileImage(
            MultipartFile file,
            String email
    ) throws IOException {

        validateFile(file);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        //🔥 Delete old profile image from S3 if exists
        if (user.getProfileImageS3Key() != null) {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(user.getProfileImageS3Key())
                            .build()
            );
        }

        //🔥 Create new S3 key
        String s3Key = IMAGE_FOLDER
                + user.getUserId() + "-"
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

        // 🔥 Update User with profile image information
        user.setProfileImageUrl(imageUrl);
        user.setProfileImageS3Key(s3Key);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    // ✅ Delete profile image
    @Transactional
    public void deleteProfileImage(String email) {

        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Delete image from S3 if exists
        if (user.getProfileImageS3Key() != null) {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(user.getProfileImageS3Key())
                            .build()
            );
        }
        
        // Clear profile image information from user
        user.setProfileImageUrl(null);
        user.setProfileImageS3Key(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
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