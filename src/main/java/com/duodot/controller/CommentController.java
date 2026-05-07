package com.duodot.controller;

import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/memories/{memoryId}/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @PostMapping
    public ServiceResponseBean addComment(
            @PathVariable Long memoryId,
            @RequestParam String description
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = commentService.addComment(memoryId, description, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @GetMapping
    public ServiceResponseBean getComments(
            @PathVariable Long memoryId
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = commentService.getCommentsByMemory(memoryId, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @PutMapping("/{commentId}")
    public ServiceResponseBean updateComment(
            @PathVariable Long memoryId,
            @PathVariable Long commentId,
            @RequestParam String description
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = commentService.updateComment(commentId, description, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @DeleteMapping("/{commentId}")
    public ServiceResponseBean deleteComment(
            @PathVariable Long memoryId,
            @PathVariable Long commentId
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = commentService.deleteComment(commentId, serviceResponseBean);
        return serviceResponseBean;
    }
}
