package com.duodot.controller;

import com.duodot.requestBean.MemoryRequestBean;
import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.service.MemoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/memories")
@RequiredArgsConstructor
public class MemoryController {
    
    private final MemoryService memoryService;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResponseBean createMemory(
            @Valid @ModelAttribute MemoryRequestBean request,
            @RequestParam(required = false) List<MultipartFile> files
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = memoryService.createMemory(request, files, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @GetMapping
    public ServiceResponseBean getMemories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = memoryService.getMemories(pageable, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @GetMapping("/{memoryId}")
    public ServiceResponseBean getMemory(
            @PathVariable Long memoryId
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = memoryService.getMemoryById(memoryId, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @PutMapping("/{memoryId}")
    public ServiceResponseBean updateMemory(
            @PathVariable Long memoryId,
            @Valid @RequestBody MemoryRequestBean request
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = memoryService.updateMemory(memoryId, request, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @PostMapping(value = "/{memoryId}/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResponseBean addMedia(
            @PathVariable Long memoryId,
            @RequestParam List<MultipartFile> files
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = memoryService.addMediaToMemory(memoryId, files, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @DeleteMapping("/{memoryId}")
    public ServiceResponseBean deleteMemory(
            @PathVariable Long memoryId
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = memoryService.deleteMemory(memoryId, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @GetMapping("/count")
    public ServiceResponseBean getTotalMemories() {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = memoryService.getTotalMemoryCount(serviceResponseBean);
        return serviceResponseBean;
    }
}
