package com.duodot.responseBean;

import com.duodot.dto.CommentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryResponseBean {
    private Long id;
    private String memoryId;
    private String pairId;
    private String userId;
    private Calendar memoryDate;
    private String description;
    private String location;
    private List<String> mediaFiles;
    private List<CommentDTO> comments;
    private String lastUpdatedBy;
    private Calendar createdAt;
    private Calendar updatedAt;
}
