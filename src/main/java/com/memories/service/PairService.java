package com.memories.service;

import com.memories.dto.PairRequestDTO;
import com.memories.dto.UserDTO;
import com.memories.entity.Pair;
import com.memories.entity.PairRequest;
import com.memories.entity.User;
import com.memories.exception.BadRequestException;
import com.memories.exception.ResourceNotFoundException;
import com.memories.repository.PairRepository;
import com.memories.repository.PairRequestRepository;
import com.memories.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PairService {
    
    private final PairRequestRepository pairRequestRepository;
    private final PairRepository pairRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    
    @Transactional
    public PairRequestDTO sendPairRequest(String receiverUsername) {
        User sender = userService.getCurrentUser();
        
        if (sender.isPaired()) {
            throw new BadRequestException("You are already paired with someone");
        }
        
        User receiver = userRepository.findByUsernameAndIsDeletedFalse(receiverUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (receiver.isPaired()) {
            throw new BadRequestException("User is already paired");
        }
        
        if (sender.getId().equals(receiver.getId())) {
            throw new BadRequestException("Cannot send pair request to yourself");
        }
        
        // Check if request already exists
        var existingRequest = pairRequestRepository.findExistingRequest(
                sender, receiver, PairRequest.RequestStatus.PENDING
        );
        
        if (existingRequest.isPresent()) {
            throw new BadRequestException("Pair request already sent");
        }
        
        var pairRequest = PairRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(PairRequest.RequestStatus.PENDING)
                .build();
        
        pairRequestRepository.save(pairRequest);
        
        return convertToPairRequestDTO(pairRequest);
    }
    
    public List<PairRequestDTO> getPendingRequests() {
        User user = userService.getCurrentUser();
        List<PairRequest> requests = pairRequestRepository.findByReceiverAndStatus(
                user, PairRequest.RequestStatus.PENDING
        );
        
        return requests.stream()
                .map(this::convertToPairRequestDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PairRequestDTO acceptPairRequest(Long requestId) {
        User receiver = userService.getCurrentUser();
        
        PairRequest pairRequest = pairRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Pair request not found"));
        
        if (!pairRequest.getReceiver().getId().equals(receiver.getId())) {
            throw new BadRequestException("Unauthorized to accept this request");
        }
        
        if (pairRequest.getStatus() != PairRequest.RequestStatus.PENDING) {
            throw new BadRequestException("Request already processed");
        }
        
        if (receiver.isPaired() || pairRequest.getSender().isPaired()) {
            throw new BadRequestException("One or both users are already paired");
        }
        
        // Update pair request
        pairRequest.setStatus(PairRequest.RequestStatus.ACCEPTED);
        pairRequest.setRespondedAt(LocalDateTime.now());
        pairRequestRepository.save(pairRequest);
        
        // Create pair
        Pair pair = Pair.builder()
                .user1(pairRequest.getSender())
                .user2(receiver)
                .status(Pair.PairStatus.ACCEPTED)
                .requestedBy(pairRequest.getSender().getId())
                .pairedAt(LocalDateTime.now())
                .build();
        
        pairRepository.save(pair);
        
        // Update users
        pairRequest.getSender().setPaired(true);
        receiver.setPaired(true);
        userRepository.save(pairRequest.getSender());
        userRepository.save(receiver);
        
        return convertToPairRequestDTO(pairRequest);
    }
    
    @Transactional
    public PairRequestDTO rejectPairRequest(Long requestId) {
        User receiver = userService.getCurrentUser();
        
        PairRequest pairRequest = pairRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Pair request not found"));
        
        if (!pairRequest.getReceiver().getId().equals(receiver.getId())) {
            throw new BadRequestException("Unauthorized to reject this request");
        }
        
        if (pairRequest.getStatus() != PairRequest.RequestStatus.PENDING) {
            throw new BadRequestException("Request already processed");
        }
        
        pairRequest.setStatus(PairRequest.RequestStatus.REJECTED);
        pairRequest.setRespondedAt(LocalDateTime.now());
        pairRequestRepository.save(pairRequest);
        
        return convertToPairRequestDTO(pairRequest);
    }
    
    public UserDTO getPairedUser() {
        User currentUser = userService.getCurrentUser();
        
        Pair pair = pairRepository.findActivePairByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("No active pair found"));
        
        User pairedUser = pair.getUser1().getId().equals(currentUser.getId()) 
                ? pair.getUser2() 
                : pair.getUser1();
        
        return modelMapper.map(pairedUser, UserDTO.class);
    }
    
    @Transactional
    public void unpair() {
        User currentUser = userService.getCurrentUser();
        
        Pair pair = pairRepository.findActivePairByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("No active pair found"));
        
        // Update users
        pair.getUser1().setPaired(false);
        pair.getUser2().setPaired(false);
        userRepository.save(pair.getUser1());
        userRepository.save(pair.getUser2());
        
        // Delete pair
        pairRepository.delete(pair);
    }
    
    private PairRequestDTO convertToPairRequestDTO(PairRequest pairRequest) {
        return PairRequestDTO.builder()
                .id(pairRequest.getId())
                .sender(modelMapper.map(pairRequest.getSender(), UserDTO.class))
                .receiver(modelMapper.map(pairRequest.getReceiver(), UserDTO.class))
                .status(pairRequest.getStatus())
                .createdAt(pairRequest.getCreatedAt())
                .respondedAt(pairRequest.getRespondedAt())
                .build();
    }
}
