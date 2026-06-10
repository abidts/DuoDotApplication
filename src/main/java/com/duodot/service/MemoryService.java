package com.duodot.service;

import com.duodot.requestBean.MemoryRequestBean;
import com.duodot.responseBean.MemoryResponseBean;
import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.entity.Memory;
import com.duodot.entity.Pair;
import com.duodot.entity.User;
import com.duodot.exception.BadRequestException;
import com.duodot.exception.ResourceNotFoundException;
import com.duodot.repository.CommentRepository;
import com.duodot.repository.MemoryRepository;
import com.duodot.repository.PairRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryService {
    
    private final MemoryRepository memoryRepository;
    private final CommentRepository commentRepository;
    private final PairRepository pairRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final MemoryMapper memoryMapper;
    
    @Transactional
    public ServiceResponseBean createMemory(String description, String location, List<MultipartFile> files, ServiceResponseBean serviceResponseBean) {
        try {
            User currentUser = userService.getCurrentUser();
            log.info("User {} creating memory", currentUser.getUserId());

            Pair pair = pairRepository.findActivePairByUserId(currentUser.getUserId())
                    .orElseThrow(() -> new BadRequestException("You must be paired to create memories"));

            Memory memory = Memory.builder()
                    .pairId(pair.getPairId())
                    .userId(currentUser.getUserId())
                    .memoryDate(Calendar.getInstance())
                    .description(description)
                    .location(location)
                    .lastUpdatedBy(currentUser.getUserId())
                    .build();

            memoryRepository.save(memory);

            // Upload files if provided
            if (files != null && !files.isEmpty()) {
                fileStorageService.uploadMemoryFiles(memory, files);
            }

            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("Memory created successfully");
            serviceResponseBean.setData(memoryMapper.toResponse(memory));
        } catch (Exception e) {
            log.error("Exception occurred while creating memory :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }
    
    @Transactional(readOnly = true)
    public ServiceResponseBean getMemories(Pageable pageable, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();
        
        Pair pair = pairRepository.findActivePairByUserId(currentUser.getUserId())
                .orElseThrow(() -> new BadRequestException("You must be paired to view memories"));

        Page<MemoryResponseBean> memories = memoryRepository.findByPairIdOrderByMemoryDateDesc(pair.getPairId(), pageable)
                .map(memoryMapper::toResponse);

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Memories retrieved successfully");
        serviceResponseBean.setData(memories);
        return serviceResponseBean;
    }
    
    @Transactional(readOnly = true)
    public ServiceResponseBean getMemoryById(String memoryId, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();

        Memory memory = memoryRepository.findByMemoryId(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));
        
        validateMemoryAccess(memory, currentUser);

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Memory retrieved successfully");
        serviceResponseBean.setData(memoryMapper.toResponse(memory));
        return serviceResponseBean;
    }
    
    @Transactional
    public ServiceResponseBean updateMemory(String memoryId, MemoryRequestBean request, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();

        Memory memory = memoryRepository.findByMemoryId(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));

        validateMemoryAccess(memory, currentUser);

        memory.setMemoryDate(toCalendar(request.getMemoryDate()));
        memory.setDescription(request.getDescription());
        memory.setLocation(request.getLocation());
        memory.setLastUpdatedBy(currentUser.getUserId());
        
        memoryRepository.save(memory);

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Memory updated successfully");
        serviceResponseBean.setData(memoryMapper.toResponse(memory));
        return serviceResponseBean;
    }
    
    @Transactional
    public ServiceResponseBean deleteMemory(String memoryId, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();

        Memory memory = memoryRepository.findByMemoryId(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));
        
        validateMemoryAccess(memory, currentUser);

        commentRepository.deleteByMemoryId(memory.getMemoryId());
        memoryRepository.delete(memory);

        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Memory deleted successfully");
        serviceResponseBean.setData(null);
        return serviceResponseBean;
    }
    
    @Transactional
    public ServiceResponseBean addMediaToMemory(String memoryId, List<MultipartFile> files, ServiceResponseBean serviceResponseBean) {
        User currentUser = userService.getCurrentUser();

        Memory memory = memoryRepository.findByMemoryId(memoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Memory not found"));

        validateMemoryAccess(memory, currentUser);

        fileStorageService.uploadMemoryFiles(memory, files);
        memory.setLastUpdatedBy(currentUser.getUserId());
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

        Long count = memoryRepository.countByPairId(pair.getPairId());
        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Total memory count");
        serviceResponseBean.setData(count);
        return serviceResponseBean;
    }
    
    private Calendar toCalendar(java.time.LocalDate date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(java.sql.Date.valueOf(date));
        return cal;
    }

    private void validateMemoryAccess(Memory memory, User user) {
        Pair pair = pairRepository.findByPairId(memory.getPairId())
                .orElseThrow(() -> new BadRequestException("Associated pair not found"));
        if (!pair.getSenderId().equals(user.getUserId()) &&
            !pair.getReceiverId().equals(user.getUserId())) {
            throw new BadRequestException("You don't have access to this memory");
        }
    }
}
