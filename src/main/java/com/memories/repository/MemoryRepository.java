package com.memories.repository;

import com.memories.entity.Memory;
import com.memories.entity.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Long> {
    Page<Memory> findByPairOrderByMemoryDateDesc(Pair pair, Pageable pageable);
    List<Memory> findByPairOrderByMemoryDateDesc(Pair pair);
    Long countByPair(Pair pair);
}
