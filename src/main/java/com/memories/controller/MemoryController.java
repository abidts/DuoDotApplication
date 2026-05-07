package com.memories.controller;

import com.memories.dto.ApiResponse;
import com.memories.dto.MemoryRequest;
import com.memories.dto.MemoryResponse;
import com.memories.service.MemoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/memories")
@RequiredArgsConstructor
public class MemoryController {
    
    private final MemoryService memoryService;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MemoryResponse>> createMemory(
            @Valid @ModelAttribute MemoryRequest request,
            @RequestParam(required = false) List<MultipartFile> files
    ) {
        MemoryResponse memory = memoryService.createMemory(request, files);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Memory created successfully", memory));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MemoryResponse>>> getMemories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MemoryResponse> memories = memoryService.getMemories(pageable);
        return ResponseEntity.ok(ApiResponse.success("Memories retrieved successfully", memories));
    }
    
    @GetMapping("/{memoryId}")
    public ResponseEntity<ApiResponse<MemoryResponse>> getMemory(
            @PathVariable Long memoryId
    ) {
        MemoryResponse memory = memoryService.getMemoryById(memoryId);
        return ResponseEntity.ok(ApiResponse.success("Memory retrieved successfully", memory));
    }
    
    @PutMapping("/{memoryId}")
    public ResponseEntity<ApiResponse<MemoryResponse>> updateMemory(
            @PathVariable Long memoryId,
            @Valid @RequestBody MemoryRequest request
    ) {
        MemoryResponse memory = memoryService.updateMemory(memoryId, request);
        return ResponseEntity.ok(ApiResponse.success("Memory updated successfully", memory));
    }
    
    @PostMapping(value = "/{memoryId}/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MemoryResponse>> addMedia(
            @PathVariable Long memoryId,
            @RequestParam List<MultipartFile> files
    ) {
        MemoryResponse memory = memoryService.addMediaToMemory(memoryId, files);
        return ResponseEntity.ok(ApiResponse.success("Media added successfully", memory));
    }
    
    @DeleteMapping("/{memoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteMemory(
            @PathVariable Long memoryId
    ) {
        memoryService.deleteMemory(memoryId);
        return ResponseEntity.ok(ApiResponse.success("Memory deleted successfully", null));
    }
    
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getTotalMemories() {
        Long count = memoryService.getTotalMemoryCount();
        return ResponseEntity.ok(ApiResponse.success("Total memory count", count));
    }
}
