package com.memories.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pairs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pair {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user1_id", nullable = false, unique = true)
    private User user1;
    
    @OneToOne
    @JoinColumn(name = "user2_id", nullable = false, unique = true)
    private User user2;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PairStatus status = PairStatus.PENDING;
    
    @Column(name = "requested_by")
    private Long requestedBy;
    
    @OneToMany(mappedBy = "pair", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Memory> memories = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "paired_at")
    private LocalDateTime pairedAt;
    
    public enum PairStatus {
        PENDING, ACCEPTED, REJECTED
    }
}
