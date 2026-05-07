package com.duodot.repository;

import com.duodot.entity.Comment;
import com.duodot.entity.Memory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMemoryOrderByCreatedAtDesc(Memory memory);
}
