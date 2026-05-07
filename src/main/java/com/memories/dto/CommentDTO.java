package com.memories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String commenterName;
    private Long commenterId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
