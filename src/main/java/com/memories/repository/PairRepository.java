package com.memories.repository;

import com.memories.entity.Pair;
import com.memories.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PairRepository extends JpaRepository<Pair, Long> {
    
    @Query("SELECT p FROM Pair p WHERE (p.user1 = :user OR p.user2 = :user) AND p.status = 'ACCEPTED'")
    Optional<Pair> findActivePairByUser(@Param("user") User user);
    
    @Query("SELECT p FROM Pair p WHERE ((p.user1.id = :userId1 AND p.user2.id = :userId2) " +
           "OR (p.user1.id = :userId2 AND p.user2.id = :userId1))")
    Optional<Pair> findByUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
