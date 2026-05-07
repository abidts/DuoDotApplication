package com.duodot.service;

import com.duodot.entity.MediaFile;
import com.duodot.entity.Memory;
import com.duodot.exception.FileStorageException;
import com.duodot.repository.MediaFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {
    
    @Autowired(required = false)
    private S3Client s3Client;
    
    private final MediaFileRepository mediaFileRepository;
    
    public FileStorageService(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }
    
    @Value("${application.aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${application.file.upload-dir}")
    private String uploadDir;
    
    public String uploadFile(MultipartFile file, String folder) {
        if (s3Client == null) {
            throw new FileStorageException("AWS S3 is not configured. Please configure AWS credentials.");
        }
        try {
            String fileName = generateFileName(file.getOriginalFilename());
            String key = folder + "/" + fileName;
            
            // Upload to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            
            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
            
        } catch (IOException e) {
            log.error("Error uploading file", e);
            throw new FileStorageException("Failed to upload file: " + e.getMessage());
        }
    }
    
    public void uploadMemoryFiles(Memory memory, List<MultipartFile> files) {
        files.forEach(file -> {
            String fileUrl = uploadFile(file, "memories/" + memory.getId());
            
            MediaFile.FileType fileType = determineFileType(file.getContentType());
            
            MediaFile mediaFile = MediaFile.builder()
                    .memory(memory)
                    .fileUrl(fileUrl)
                    .fileName(file.getOriginalFilename())
                    .fileType(fileType)
                    .fileSize(file.getSize())
                    .build();
            
            mediaFileRepository.save(mediaFile);
        });
    }
    
    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
    
    private MediaFile.FileType determineFileType(String contentType) {
        if (contentType == null) {
            return MediaFile.FileType.IMAGE;
        }
        
        if (contentType.startsWith("video/")) {
            return MediaFile.FileType.VIDEO;
        }
        return MediaFile.FileType.IMAGE;
    }
}
