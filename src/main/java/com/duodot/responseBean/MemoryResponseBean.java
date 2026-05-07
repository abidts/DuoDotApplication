package com.duodot.responseBean;

import com.duodot.dto.CommentDTO;
import com.duodot.dto.MediaFileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryResponseBean {
    private Long id;
    private String creatorName;
    private Long creatorId;
    private LocalDate memoryDate;
    private String description;
    private String location;
    private List<MediaFileDTO> mediaFiles;
    private List<CommentDTO> comments;
    private String lastUpdatedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
