package com.duodot.repository;

import com.duodot.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByCommentId(String commentId);
    List<Comment> findByMemoryIdOrderByCreatedAtDesc(String memoryId);
    void deleteByMemoryId(String memoryId);
}
