package com.duodot.service;

import com.duodot.dto.CommentDTO;
import com.duodot.dto.MediaFileDTO;
import com.duodot.responseBean.MemoryResponseBean;
import com.duodot.entity.Comment;
import com.duodot.entity.MediaFile;
import com.duodot.entity.Memory;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MemoryMapper {
    
    public MemoryResponseBean toResponse(Memory memory) {
        return MemoryResponseBean.builder()
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
