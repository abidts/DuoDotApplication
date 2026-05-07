package com.duodot.service;

import com.duodot.dto.CommentDTO;
import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.entity.Comment;
import com.duodot.entity.Memory;
import com.duodot.entity.User;
import com.duodot.exception.BadRequestException;
import com.duodot.exception.ResourceNotFoundException;
import com.duodot.repository.CommentRepository;
import com.duodot.repository.MemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final MemoryRepository memoryRepository;
    private final UserService userService;
    
    @Transactional
    public ServiceResponseBean addComment(Long memoryId, String description, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();
        
        Memory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));
        
        validateMemoryAccess(memory, currentUser);
        
        Comment comment = Comment.builder()
                .memory(memory)
                .commenter(currentUser)
                .description(description)
                .build();
        
        commentRepository.save(comment);

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Comment added successfully");
        serviceResponseBean.setData(convertToDTO(comment));
        return serviceResponseBean;
    }
    
    public ServiceResponseBean getCommentsByMemory(Long memoryId, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();
        
        Memory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));
        
        validateMemoryAccess(memory, currentUser);

        List<CommentDTO> comments = commentRepository.findByMemoryOrderByCreatedAtDesc(memory)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Comments retrieved successfully");
        serviceResponseBean.setData(comments);
        return serviceResponseBean;
    }
    
    @Transactional
    public ServiceResponseBean updateComment(Long commentId, String description, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        if (!comment.getCommenter().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only update your own comments");
        }
        
        comment.setDescription(description);
        commentRepository.save(comment);

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Comment updated successfully");
        serviceResponseBean.setData(convertToDTO(comment));
        return serviceResponseBean;
    }
    
    @Transactional
    public ServiceResponseBean deleteComment(Long commentId, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        if (!comment.getCommenter().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own comments");
        }
        
        commentRepository.delete(comment);

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Comment deleted successfully");
        serviceResponseBean.setData(null);
        return serviceResponseBean;
    }
    
    private void validateMemoryAccess(Memory memory, User user) {
        var pair = memory.getPair();
        if (!pair.getUser1().getId().equals(user.getId()) &&
            !pair.getUser2().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have access to this memory");
        }
    }
    
    private CommentDTO convertToDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .commenterName(comment.getCommenter().getFirstName() + " " + comment.getCommenter().getLastName())
                .commenterId(comment.getCommenter().getId())
                .description(comment.getDescription())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
