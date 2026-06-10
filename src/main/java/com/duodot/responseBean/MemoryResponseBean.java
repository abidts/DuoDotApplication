package com.duodot.responseBean;

import com.duodot.dto.CommentDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@JsonInclude(JsonInclude.Include.NON_NULL)

public class MemoryResponseBean {
    
    @JsonProperty(value = "memory_id")
    private String memoryId;
    @JsonProperty(value = "pair_id")
    private String pairId;
    @JsonProperty(value = "user_id")
    private String userId;
    @JsonProperty(value = "memory_date")
    private String memoryDate;
    private String description;
    private String location;
    @JsonProperty(value = "media_files")
    private List<String> mediaFiles;
    @JsonProperty(value = "created_date")
    private String createdDate;
   
}
