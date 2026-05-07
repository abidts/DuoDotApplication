package com.duodot.repository;

import com.duodot.entity.PairRequest;
import com.duodot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PairRequestRepository extends JpaRepository<PairRequest, Long> {
    
    List<PairRequest> findByReceiverAndStatus(User receiver, PairRequest.RequestStatus status);
    
    List<PairRequest> findBySenderAndStatus(User sender, PairRequest.RequestStatus status);
    
    @Query("SELECT pr FROM PairRequest pr WHERE " +
           "((pr.sender = :user1 AND pr.receiver = :user2) OR " +
           "(pr.sender = :user2 AND pr.receiver = :user1)) AND " +
           "pr.status = :status")
    Optional<PairRequest> findExistingRequest(
            @Param("user1") User user1,
            @Param("user2") User user2,
            @Param("status") PairRequest.RequestStatus status
    );
}
