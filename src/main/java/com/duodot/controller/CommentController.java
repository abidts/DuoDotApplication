package com.duodot.controller;

import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ServiceResponseBean addComment(
            @RequestParam String memoryId,
            @RequestParam String description
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = commentService.addComment(memoryId, description, serviceResponseBean);
        return serviceResponseBean;
    }

    @GetMapping
    public ServiceResponseBean getComments(
            @RequestParam String memoryId
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = commentService.getCommentsByMemory(memoryId, serviceResponseBean);
        return serviceResponseBean;
    }

    @PutMapping("/update")
    public ServiceResponseBean updateComment(
            @RequestParam String commentId,
            @RequestParam String description
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = commentService.updateComment(commentId, description, serviceResponseBean);
        return serviceResponseBean;
    }

    @DeleteMapping("/delete")
    public ServiceResponseBean deleteComment(
            @RequestParam String commentId
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = commentService.deleteComment(commentId, serviceResponseBean);
        return serviceResponseBean;
    }
}
