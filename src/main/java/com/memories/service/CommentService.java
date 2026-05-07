package com.memories.service;

import com.memories.dto.CommentDTO;
import com.memories.entity.Comment;
import com.memories.entity.Memory;
import com.memories.entity.User;
import com.memories.exception.BadRequestException;
import com.memories.exception.ResourceNotFoundException;
import com.memories.repository.CommentRepository;
import com.memories.repository.MemoryRepository;
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
    public CommentDTO addComment(Long memoryId, String description) {
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
        
        return convertToDTO(comment);
    }
    
    public List<CommentDTO> getCommentsByMemory(Long memoryId) {
        User currentUser = userService.getCurrentUser();
        
        Memory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));
        
        validateMemoryAccess(memory, currentUser);
        
        return commentRepository.findByMemoryOrderByCreatedAtDesc(memory)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CommentDTO updateComment(Long commentId, String description) {
        User currentUser = userService.getCurrentUser();
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        if (!comment.getCommenter().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only update your own comments");
        }
        
        comment.setDescription(description);
        commentRepository.save(comment);
        
        return convertToDTO(comment);
    }
    
    @Transactional
    public void deleteComment(Long commentId) {
        User currentUser = userService.getCurrentUser();
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        if (!comment.getCommenter().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own comments");
        }
        
        commentRepository.delete(comment);
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
