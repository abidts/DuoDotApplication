package com.memories.dto;

import com.memories.entity.MediaFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFileDTO {
    private Long id;
    private String fileUrl;
    private String fileName;
    private MediaFile.FileType fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
}
