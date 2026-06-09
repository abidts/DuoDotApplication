package com.duodot.repository;

import com.duodot.entity.Memory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Long> {
    Optional<Memory> findByMemoryId(String memoryId);
    Page<Memory> findByPairIdOrderByMemoryDateDesc(String pairId, Pageable pageable);
    Long countByPairId(String pairId);
}
