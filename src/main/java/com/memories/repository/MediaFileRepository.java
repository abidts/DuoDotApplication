package com.memories.repository;

import com.memories.entity.MediaFile;
import com.memories.entity.Memory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
    List<MediaFile> findByMemory(Memory memory);
}
