package com.duodot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "memories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Memory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "memory_id", unique = true)
    private String memoryId;

    @Column(name = "pair_id", nullable = false)
    private String pairId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "memory_date")
    private Calendar memoryDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "media_files", columnDefinition = "jsonb")
    private List<String> mediaFiles;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Calendar createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Calendar updatedAt;

    @PrePersist
    public void generateMemoryId() {
        if (memoryId == null) {
            memoryId = "memo_" + UUID.randomUUID().toString().replace("-", "");
        }
    }
}
