package com.memories.service;

import com.memories.dto.MemoryRequest;
import com.memories.dto.MemoryResponse;
import com.memories.entity.Memory;
import com.memories.entity.Pair;
import com.memories.entity.User;
import com.memories.exception.BadRequestException;
import com.memories.exception.ResourceNotFoundException;
import com.memories.repository.MemoryRepository;
import com.memories.repository.PairRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoryService {
    
    private final MemoryRepository memoryRepository;
    private final PairRepository pairRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final MemoryMapper memoryMapper;
    
    @Transactional
    public MemoryResponse createMemory(MemoryRequest request, List<MultipartFile> files) {
        User currentUser = userService.getCurrentUser();
        
        Pair pair = pairRepository.findActivePairByUser(currentUser)
                .orElseThrow(() -> new BadRequestException("You must be paired to create memories"));
        
        Memory memory = Memory.builder()
                .pair(pair)
                .creator(currentUser)
                .memoryDate(request.getMemoryDate())
                .description(request.getDescription())
                .location(request.getLocation())
                .lastUpdatedBy(currentUser)
                .build();
        
        memoryRepository.save(memory);
        
        // Upload files if provided
        if (files != null && !files.isEmpty()) {
            fileStorageService.uploadMemoryFiles(memory, files);
        }
        
        return memoryMapper.toResponse(memory);
    }
    
    public Page<MemoryResponse> getMemories(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        
        Pair pair = pairRepository.findActivePairByUser(currentUser)
                .orElseThrow(() -> new BadRequestException("You must be paired to view memories"));
        
        return memoryRepository.findByPairOrderByMemoryDateDesc(pair, pageable)
                .map(memoryMapper::toResponse);
    }
    
    public MemoryResponse getMemoryById(Long memoryId) {
        User currentUser = userService.getCurrentUser();
        
        Memory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));
        
        validateMemoryAccess(memory, currentUser);
        
        return memoryMapper.toResponse(memory);
    }
    
    @Transactional
    public MemoryResponse updateMemory(Long memoryId, MemoryRequest request) {
        User currentUser = userService.getCurrentUser();
        
        Memory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));
        
        validateMemoryAccess(memory, currentUser);
        
        memory.setMemoryDate(request.getMemoryDate());
        memory.setDescription(request.getDescription());
        memory.setLocation(request.getLocation());
        memory.setLastUpdatedBy(currentUser);
        
        memoryRepository.save(memory);
        
        return memoryMapper.toResponse(memory);
    }
    
    @Transactional
    public void deleteMemory(Long memoryId) {
        User currentUser = userService.getCurrentUser();
        
        Memory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));
        
        validateMemoryAccess(memory, currentUser);
        
        memoryRepository.delete(memory);
    }
    
    @Transactional
    public MemoryResponse addMediaToMemory(Long memoryId, List<MultipartFile> files) {
        User currentUser = userService.getCurrentUser();
        
        Memory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));
        
        validateMemoryAccess(memory, currentUser);
        
        fileStorageService.uploadMemoryFiles(memory, files);
        memory.setLastUpdatedBy(currentUser);
        memoryRepository.save(memory);
        
        return memoryMapper.toResponse(memory);
    }
    
    public Long getTotalMemoryCount() {
        User currentUser = userService.getCurrentUser();
        
        Pair pair = pairRepository.findActivePairByUser(currentUser)
                .orElseThrow(() -> new BadRequestException("You must be paired to view memory count"));
        
        return memoryRepository.countByPair(pair);
    }
    
    private void validateMemoryAccess(Memory memory, User user) {
        Pair memoryPair = memory.getPair();
        
        if (!memoryPair.getUser1().getId().equals(user.getId()) &&
            !memoryPair.getUser2().getId().equals(user.getId())) {
            throw new BadRequestException("You don't have access to this memory");
        }
    }
}
