package com.memories.repository;

import com.memories.entity.Comment;
import com.memories.entity.Memory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMemoryOrderByCreatedAtDesc(Memory memory);
}
