package com.duodot.service;

import com.duodot.dto.PairRequestDTO;
import com.duodot.dto.UserDTO;
import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.entity.Pair;
import com.duodot.entity.PairRequest;
import com.duodot.entity.User;
import com.duodot.exception.BadRequestException;
import com.duodot.exception.ResourceNotFoundException;
import com.duodot.repository.PairRepository;
import com.duodot.repository.PairRequestRepository;
import com.duodot.repository.UserRepository;
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
    public ServiceResponseBean sendPairRequest(String receiverUsername, ServiceResponseBean serviceResponseBean) {
        User sender = userService.getCurrentUser();
        
        if (sender.getPaired()) {
            throw new BadRequestException("You are already paired with someone");
        }
        
        User receiver = userRepository.findByUsernameAndIsDeletedFalse(receiverUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (receiver.getPaired()) {
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

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Pair request sent successfully");
        serviceResponseBean.setData(convertToPairRequestDTO(pairRequest));
        return serviceResponseBean;
    }
    
    public ServiceResponseBean getPendingRequests(ServiceResponseBean serviceResponseBean) {
        User user = userService.getCurrentUser();
        List<PairRequest> requests = pairRequestRepository.findByReceiverAndStatus(
                user, PairRequest.RequestStatus.PENDING
        );

        List<PairRequestDTO> pendingRequests = requests.stream()
                .map(this::convertToPairRequestDTO)
                .collect(Collectors.toList());

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Pending requests retrieved");
        serviceResponseBean.setData(pendingRequests);
        return serviceResponseBean;
    }
    
    @Transactional
    public ServiceResponseBean acceptPairRequest(Long requestId, ServiceResponseBean serviceResponseBean) {
        User receiver = userService.getCurrentUser();
        
        PairRequest pairRequest = pairRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Pair request not found"));
        
        if (!pairRequest.getReceiver().getId().equals(receiver.getId())) {
            throw new BadRequestException("Unauthorized to accept this request");
        }
        
        if (pairRequest.getStatus() != PairRequest.RequestStatus.PENDING) {
            throw new BadRequestException("Request already processed");
        }
        
        if (receiver.getPaired() || pairRequest.getSender().getPaired()) {
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

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Pair request accepted");
        serviceResponseBean.setData(convertToPairRequestDTO(pairRequest));
        return serviceResponseBean;
    }
    
    @Transactional
    public ServiceResponseBean rejectPairRequest(Long requestId, ServiceResponseBean serviceResponseBean) {
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

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Pair request rejected");
        serviceResponseBean.setData(convertToPairRequestDTO(pairRequest));
        return serviceResponseBean;
    }
    
    public ServiceResponseBean getPairedUser(ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();
        
        Pair pair = pairRepository.findActivePairByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("No active pair found"));
        
        User pairedUser = pair.getUser1().getId().equals(currentUser.getId()) 
                ? pair.getUser2() 
                : pair.getUser1();

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Paired user retrieved");
        serviceResponseBean.setData(modelMapper.map(pairedUser, UserDTO.class));
        return serviceResponseBean;
    }
    
    @Transactional
    public ServiceResponseBean unpair(ServiceResponseBean serviceResponseBean) {
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

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Unpaired successfully");
        serviceResponseBean.setData(null);
        return serviceResponseBean;
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
