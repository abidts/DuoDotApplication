package com.duodot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private String commentId;
    private String memoryId;
    private String userId;
    private String description;
    private Calendar createdAt;
    private Calendar updatedAt;
}
