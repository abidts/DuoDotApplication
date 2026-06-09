package com.duodot.service;

import com.duodot.dto.CommentDTO;
import com.duodot.enums.PairNotificationTypeEnum;
import com.duodot.requestBean.NotificationMessage;
import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.entity.Comment;
import com.duodot.entity.Memory;
import com.duodot.entity.Pair;
import com.duodot.entity.User;
import com.duodot.exception.BadRequestException;
import com.duodot.exception.ResourceNotFoundException;
import com.duodot.repository.CommentRepository;
import com.duodot.repository.MemoryRepository;
import com.duodot.repository.PairRepository;
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
    private final PairRepository pairRepository;
    private final UserService userService;
    private final NotificationProducer notificationProducer;

    @Transactional
    public ServiceResponseBean addComment(String memoryId, String description, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();

        Memory memory = memoryRepository.findByMemoryId(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));

        validateMemoryAccess(memory, currentUser);

        Comment comment = Comment.builder()
                .memoryId(memory.getMemoryId())
                .userId(currentUser.getUserId())
                .description(description)
                .build();

        commentRepository.save(comment);

        Pair pair = pairRepository.findByPairId(memory.getPairId()).orElse(null);
        if (pair != null) {
            String receiverId = pair.getSenderId().equals(currentUser.getUserId())
                    ? pair.getReceiverId()
                    : pair.getSenderId();
            notificationProducer.publish(NotificationMessage.builder()
                    .senderId(currentUser.getUserId())
                    .receiverId(receiverId)
                    .actorUsername(currentUser.getUserId())
                    .type(PairNotificationTypeEnum.COMMENT_ADDED)
                    .build());
        }

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Comment added successfully");
        serviceResponseBean.setData(convertToDTO(comment));
        return serviceResponseBean;
    }

    public ServiceResponseBean getCommentsByMemory(String memoryId, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();

        Memory memory = memoryRepository.findByMemoryId(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));

        validateMemoryAccess(memory, currentUser);

        List<CommentDTO> comments = commentRepository.findByMemoryIdOrderByCreatedAtDesc(memory.getMemoryId())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Comments retrieved successfully");
        serviceResponseBean.setData(comments);
        return serviceResponseBean;
    }

    @Transactional
    public ServiceResponseBean updateComment(String commentId, String description, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();

        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUserId().equals(currentUser.getUserId())) {
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
    public ServiceResponseBean deleteComment(String commentId, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();

        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUserId().equals(currentUser.getUserId())) {
            throw new BadRequestException("You can only delete your own comments");
        }

        commentRepository.delete(comment);

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Comment deleted successfully");
        serviceResponseBean.setData(null);
        return serviceResponseBean;
    }

    private void validateMemoryAccess(Memory memory, User user) {
        Pair pair = pairRepository.findByPairId(memory.getPairId())
                .orElseThrow(() -> new BadRequestException("Associated pair not found"));
        if (!pair.getSenderId().equals(user.getUserId()) &&
            !pair.getReceiverId().equals(user.getUserId())) {
            throw new BadRequestException("You don't have access to this memory");
        }
    }

    private CommentDTO convertToDTO(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .memoryId(comment.getMemoryId())
                .userId(comment.getUserId())
                .description(comment.getDescription())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
