package com.memories.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryRequest {
    
    @NotNull(message = "Memory date is required")
    private LocalDate memoryDate;
    
    private String description;
    private String location;
}
