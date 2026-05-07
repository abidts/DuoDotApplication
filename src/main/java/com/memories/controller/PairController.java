package com.memories.controller;

import com.memories.dto.ApiResponse;
import com.memories.dto.PairRequestDTO;
import com.memories.dto.UserDTO;
import com.memories.service.PairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pairs")
@RequiredArgsConstructor
public class PairController {
    
    private final PairService pairService;
    
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<PairRequestDTO>> sendPairRequest(
            @RequestParam String username
    ) {
        PairRequestDTO request = pairService.sendPairRequest(username);
        return ResponseEntity.ok(ApiResponse.success("Pair request sent successfully", request));
    }
    
    @GetMapping("/requests/pending")
    public ResponseEntity<ApiResponse<List<PairRequestDTO>>> getPendingRequests() {
        List<PairRequestDTO> requests = pairService.getPendingRequests();
        return ResponseEntity.ok(ApiResponse.success("Pending requests retrieved", requests));
    }
    
    @PutMapping("/requests/{requestId}/accept")
    public ResponseEntity<ApiResponse<PairRequestDTO>> acceptRequest(
            @PathVariable Long requestId
    ) {
        PairRequestDTO request = pairService.acceptPairRequest(requestId);
        return ResponseEntity.ok(ApiResponse.success("Pair request accepted", request));
    }
    
    @PutMapping("/requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<PairRequestDTO>> rejectRequest(
            @PathVariable Long requestId
    ) {
        PairRequestDTO request = pairService.rejectPairRequest(requestId);
        return ResponseEntity.ok(ApiResponse.success("Pair request rejected", request));
    }
    
    @GetMapping("/partner")
    public ResponseEntity<ApiResponse<UserDTO>> getPairedUser() {
        UserDTO user = pairService.getPairedUser();
        return ResponseEntity.ok(ApiResponse.success("Paired user retrieved", user));
    }
    
    @DeleteMapping("/unpair")
    public ResponseEntity<ApiResponse<Void>> unpair() {
        pairService.unpair();
        return ResponseEntity.ok(ApiResponse.success("Unpaired successfully", null));
    }
}
