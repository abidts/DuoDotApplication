package com.duodot.repository;

import com.duodot.entity.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PairRepository extends JpaRepository<Pair, Long> {

    @Query("SELECT p FROM Pair p WHERE (p.senderId = :userId OR p.receiverId = :userId) AND p.status = 'PAIRED'")
    Optional<Pair> findActivePairByUserId(@Param("userId") String userId);

    @Query("SELECT p FROM Pair p WHERE (p.senderId = :userId OR p.receiverId = :userId) AND p.status = 'REQUEST_SENT'")
    List<Pair> findAllRequestsByUserId(@Param("userId") String userId);

    @Query("SELECT p FROM Pair p WHERE (p.senderId = :userId1 AND p.receiverId = :userId2) OR (p.senderId = :userId2 AND p.receiverId = :userId1)")
    Optional<Pair> findExistingPair(@Param("userId1") String userId1, @Param("userId2") String userId2);
}
