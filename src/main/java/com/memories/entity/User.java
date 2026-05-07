package com.memories.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "profile_picture")
    private String profilePicture;
    
    private String city;
    
    private String state;
    
    @Column(name = "pin_id")
    private String pinId;
    
    @Column(name = "is_paired")
    private boolean isPaired = false;
    
    @Column(name = "is_deleted")
    private boolean isDeleted = false;
    
    @OneToOne(mappedBy = "user1", cascade = CascadeType.ALL)
    private Pair pairAsUser1;
    
    @OneToOne(mappedBy = "user2", cascade = CascadeType.ALL)
    private Pair pairAsUser2;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
