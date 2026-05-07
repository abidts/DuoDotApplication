package com.duodot.repository;

import com.duodot.entity.MediaFile;
import com.duodot.entity.Memory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {
    List<MediaFile> findByMemory(Memory memory);
}
