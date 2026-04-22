package com.maqc.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.text.Normalizer;
import java.util.UUID;

@Service
@Slf4j
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final String endpoint;

    public S3Service(
            @Value("${supabase.s3.endpoint}") String endpoint,
            @Value("${supabase.s3.region}") String region,
            @Value("${supabase.s3.access-key}") String accessKey,
            @Value("${supabase.s3.secret-key}") String secretKey,
            @Value("${supabase.s3.bucket}") String bucketName) {
        
        this.bucketName = bucketName;
        this.endpoint = endpoint;

        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .forcePathStyle(true) // Required for Supabase
                .build();
    }

    /**
     * Sanitize a filename: strip accents, replace spaces/special chars with hyphens, lowercase.
     * Example: "Théâtre - Société des poètes disparus 2026.png" → "theatre-societe-des-poetes-disparus-2026.png"
     */
    private String sanitizeFileName(String originalName) {
        if (originalName == null || originalName.isBlank()) {
            return "file";
        }
        // Separate the extension
        String name = originalName;
        String ext = "";
        int dotIdx = originalName.lastIndexOf('.');
        if (dotIdx > 0) {
            name = originalName.substring(0, dotIdx);
            ext = originalName.substring(dotIdx); // includes the dot
        }
        // Normalize unicode → strip diacritics (é→e, â→a, etc.)
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        // Replace any non-alphanumeric character (except hyphens) with a hyphen
        normalized = normalized.replaceAll("[^a-zA-Z0-9\\-]", "-");
        // Collapse multiple hyphens and trim leading/trailing hyphens
        normalized = normalized.replaceAll("-{2,}", "-").replaceAll("^-|-$", "");
        // Lowercase
        normalized = normalized.toLowerCase();
        if (normalized.isEmpty()) {
            normalized = "file";
        }
        return normalized + ext.toLowerCase();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String sanitized = sanitizeFileName(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "_" + sanitized;
        
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // Construct the public URL
            // Supabase S3 URL structure: [project-url]/storage/v1/object/public/[bucket]/[filename]
            // We can derive the project URL from the endpoint
            String projectUrl = endpoint.replace(".storage.supabase.co/storage/v1/s3", ".supabase.co");
            return projectUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
        } catch (Exception e) {
            log.error("Failed to upload file to S3: {}", e.getMessage());
            throw new IOException("Failed to upload file to S3", e);
        }
    }
}
