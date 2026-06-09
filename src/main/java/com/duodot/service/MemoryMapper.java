package com.duodot.service;

import com.duodot.dto.CommentDTO;
import com.duodot.repository.CommentRepository;
import com.duodot.responseBean.MemoryResponseBean;
import com.duodot.entity.Comment;
import com.duodot.entity.Memory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MemoryMapper {

    private final CommentRepository commentRepository;

    public MemoryResponseBean toResponse(Memory memory) {
        List<CommentDTO> comments = commentRepository
                .findByMemoryIdOrderByCreatedAtDesc(memory.getMemoryId())
                .stream()
                .map(this::toCommentDTO)
                .collect(Collectors.toList());

        return MemoryResponseBean.builder()
                .id(memory.getId())
                .memoryId(memory.getMemoryId())
                .pairId(memory.getPairId())
                .userId(memory.getUserId())
                .memoryDate(memory.getMemoryDate())
                .description(memory.getDescription())
                .location(memory.getLocation())
                .mediaFiles(memory.getMediaFiles())
                .comments(comments)
                .lastUpdatedBy(memory.getLastUpdatedBy())
                .createdAt(memory.getCreatedAt())
                .updatedAt(memory.getUpdatedAt())
                .build();
    }

    private CommentDTO toCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .memoryId(comment.getMemoryId())
                .userId(comment.getUserId())
                .description(comment.getDescription())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
