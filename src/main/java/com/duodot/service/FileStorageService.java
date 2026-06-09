package com.duodot.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.duodot.entity.MediaFile;
import com.duodot.entity.Memory;
import com.duodot.exception.FileStorageException;
import com.duodot.repository.MediaFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final MediaFileRepository mediaFileRepository;
    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folder) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "auto"
                    )
            );
            return (String) result.get("secure_url");
        } catch (IOException e) {
            log.error("Error uploading file to Cloudinary", e);
            throw new FileStorageException("Failed to upload file: " + e.getMessage());
        }
    }

    public void uploadMemoryFiles(Memory memory, List<MultipartFile> files) {
        files.forEach(file -> {
            String fileUrl = uploadFile(file, "memories/" + memory.getId());

            MediaFile.FileType fileType = determineFileType(file.getContentType());

            MediaFile mediaFile = MediaFile.builder()
                    .memoryId(memory.getMemoryId())
                    .fileUrl(fileUrl)
                    .fileName(file.getOriginalFilename())
                    .fileType(fileType)
                    .fileSize(file.getSize())
                    .build();

            mediaFileRepository.save(mediaFile);
        });
    }

    private MediaFile.FileType determineFileType(String contentType) {
        if (contentType != null && contentType.startsWith("video/")) {
            return MediaFile.FileType.VIDEO;
        }
        return MediaFile.FileType.IMAGE;
    }
}
