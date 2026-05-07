package com.duodot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "media_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaFile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id", nullable = false)
    private Memory memory;
    
    @Column(name = "file_url", nullable = false)
    private String fileUrl;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type")
    private FileType fileType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;
    
    public enum FileType {
        IMAGE, VIDEO
    }
}
