package com.duodot.service;

import com.duodot.requestBean.MemoryRequestBean;
import com.duodot.responseBean.MemoryResponseBean;
import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.entity.Memory;
import com.duodot.entity.Pair;
import com.duodot.entity.User;
import com.duodot.exception.BadRequestException;
import com.duodot.exception.ResourceNotFoundException;
import com.duodot.repository.MemoryRepository;
import com.duodot.repository.PairRepository;
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
    public ServiceResponseBean createMemory(MemoryRequestBean request, List<MultipartFile> files, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();
        
        Pair pair = pairRepository.findActivePairByUserId(currentUser.getUserId())
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

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Memory created successfully");
        serviceResponseBean.setData(memoryMapper.toResponse(memory));
        return serviceResponseBean;
    }
    
    @Transactional(readOnly = true)
    public ServiceResponseBean getMemories(Pageable pageable, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();
        
        Pair pair = pairRepository.findActivePairByUserId(currentUser.getUserId())
                .orElseThrow(() -> new BadRequestException("You must be paired to view memories"));

        Page<MemoryResponseBean> memories = memoryRepository.findByPairOrderByMemoryDateDesc(pair, pageable)
                .map(memoryMapper::toResponse);

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Memories retrieved successfully");
        serviceResponseBean.setData(memories);
        return serviceResponseBean;
    }
    
    @Transactional(readOnly = true)
    public ServiceResponseBean getMemoryById(Long memoryId, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();
        
        Memory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));
        
        validateMemoryAccess(memory, currentUser);

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Memory retrieved successfully");
        serviceResponseBean.setData(memoryMapper.toResponse(memory));
        return serviceResponseBean;
    }
    
    @Transactional
    public ServiceResponseBean updateMemory(Long memoryId, MemoryRequestBean request, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();
        
        Memory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));
        
        validateMemoryAccess(memory, currentUser);
        
        memory.setMemoryDate(request.getMemoryDate());
        memory.setDescription(request.getDescription());
        memory.setLocation(request.getLocation());
        memory.setLastUpdatedBy(currentUser);
        
        memoryRepository.save(memory);

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Memory updated successfully");
        serviceResponseBean.setData(memoryMapper.toResponse(memory));
        return serviceResponseBean;
    }
    
    @Transactional
    public ServiceResponseBean deleteMemory(Long memoryId, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();
        
        Memory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));
        
        validateMemoryAccess(memory, currentUser);
        
        memoryRepository.delete(memory);

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Memory deleted successfully");
        serviceResponseBean.setData(null);
        return serviceResponseBean;
    }
    
    @Transactional
    public ServiceResponseBean addMediaToMemory(Long memoryId, List<MultipartFile> files, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();
        
        Memory memory = memoryRepository.findById(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));
        
        validateMemoryAccess(memory, currentUser);
        
        fileStorageService.uploadMemoryFiles(memory, files);
        memory.setLastUpdatedBy(currentUser);
        memoryRepository.save(memory);

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Media added successfully");
        serviceResponseBean.setData(memoryMapper.toResponse(memory));
        return serviceResponseBean;
    }
    
    public ServiceResponseBean getTotalMemoryCount(ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();
        
        Pair pair = pairRepository.findActivePairByUserId(currentUser.getUserId())
                .orElseThrow(() -> new BadRequestException("You must be paired to view memory count"));

        Long count = memoryRepository.countByPair(pair);
        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Total memory count");
        serviceResponseBean.setData(count);
        return serviceResponseBean;
    }
    
    private void validateMemoryAccess(Memory memory, User user) {
        Pair memoryPair = memory.getPair();
        if (!memoryPair.getSenderId().equals(user.getUserId()) &&
            !memoryPair.getReceiverId().equals(user.getUserId())) {
            throw new BadRequestException("You don't have access to this memory");
        }
    }
}
