package com.duodot.service;

import com.duodot.entity.MediaFile;
import com.duodot.entity.Memory;
import com.duodot.exception.FileStorageException;
import com.duodot.repository.MediaFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private final MediaFileRepository mediaFileRepository;

    @Value("${application.file.upload-dir}")
    private String uploadDir;

    public FileStorageService(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    public String uploadFile(MultipartFile file, String folder) {
        try {
            String fileName = generateFileName(file.getOriginalFilename());
            Path targetDir = Paths.get(uploadDir, folder);
            
            // Create directories if they don't exist
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            
            Path targetPath = targetDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath);
            
            // Return relative path for storage
            return "/uploads/" + folder + "/" + fileName;
            
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
