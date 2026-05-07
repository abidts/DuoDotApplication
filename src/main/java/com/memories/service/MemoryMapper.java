package com.memories.service;

import com.memories.dto.CommentDTO;
import com.memories.dto.MediaFileDTO;
import com.memories.dto.MemoryResponse;
import com.memories.entity.Comment;
import com.memories.entity.MediaFile;
import com.memories.entity.Memory;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MemoryMapper {
    
    public MemoryResponse toResponse(Memory memory) {
        return MemoryResponse.builder()
                .id(memory.getId())
                .creatorName(memory.getCreator().getFirstName() + " " + memory.getCreator().getLastName())
                .creatorId(memory.getCreator().getId())
                .memoryDate(memory.getMemoryDate())
                .description(memory.getDescription())
                .location(memory.getLocation())
                .mediaFiles(memory.getMediaFiles().stream()
                        .map(this::toMediaFileDTO)
                        .collect(Collectors.toList()))
                .comments(memory.getComments().stream()
                        .map(this::toCommentDTO)
                        .collect(Collectors.toList()))
                .lastUpdatedByName(memory.getLastUpdatedBy() != null 
                        ? memory.getLastUpdatedBy().getFirstName() + " " + memory.getLastUpdatedBy().getLastName()
                        : null)
                .createdAt(memory.getCreatedAt())
                .updatedAt(memory.getUpdatedAt())
                .build();
    }
    
    private MediaFileDTO toMediaFileDTO(MediaFile mediaFile) {
        return MediaFileDTO.builder()
                .id(mediaFile.getId())
                .fileUrl(mediaFile.getFileUrl())
                .fileName(mediaFile.getFileName())
                .fileType(mediaFile.getFileType())
                .fileSize(mediaFile.getFileSize())
                .uploadedAt(mediaFile.getUploadedAt())
                .build();
    }
    
    private CommentDTO toCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .commenterName(comment.getCommenter().getFirstName() + " " + comment.getCommenter().getLastName())
                .commenterId(comment.getCommenter().getId())
                .description(comment.getDescription())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
