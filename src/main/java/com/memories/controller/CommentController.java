package com.memories.controller;

import com.memories.dto.ApiResponse;
import com.memories.dto.CommentDTO;
import com.memories.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/memories/{memoryId}/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<CommentDTO>> addComment(
            @PathVariable Long memoryId,
            @RequestParam String description
    ) {
        CommentDTO comment = commentService.addComment(memoryId, description);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment added successfully", comment));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getComments(
            @PathVariable Long memoryId
    ) {
        List<CommentDTO> comments = commentService.getCommentsByMemory(memoryId);
        return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", comments));
    }
    
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDTO>> updateComment(
            @PathVariable Long memoryId,
            @PathVariable Long commentId,
            @RequestParam String description
    ) {
        CommentDTO comment = commentService.updateComment(commentId, description);
        return ResponseEntity.ok(ApiResponse.success("Comment updated successfully", comment));
    }
    
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long memoryId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
    }
}
