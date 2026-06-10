package com.duodot.controller;

import com.duodot.requestBean.MemoryRequestBean;
import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.service.MemoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/memories")
@RequiredArgsConstructor
@Validated
public class MemoryController {

    private final MemoryService memoryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResponseBean createMemory(
            @RequestParam("description") @NotBlank(message = "Description cannot be blank") String description,
            @RequestParam("location") @NotBlank(message = "Location cannot be blank") String location,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = memoryService.createMemory(description, location, files, serviceResponseBean);
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

    @GetMapping("/fetch")
    public ServiceResponseBean getMemory(
            @RequestParam String memoryId
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = memoryService.getMemoryById(memoryId, serviceResponseBean);
        return serviceResponseBean;
    }

    @PutMapping("/update")
    public ServiceResponseBean updateMemory(
            @RequestParam String memoryId,
            @Valid @RequestBody MemoryRequestBean request
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = memoryService.updateMemory(memoryId, request, serviceResponseBean);
        return serviceResponseBean;
    }

    @PostMapping(value = "/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResponseBean addMedia(
            @RequestParam String memoryId,
            @RequestParam List<MultipartFile> files
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = memoryService.addMediaToMemory(memoryId, files, serviceResponseBean);
        return serviceResponseBean;
    }

    @DeleteMapping("/delete")
    public ServiceResponseBean deleteMemory(
            @RequestParam String memoryId
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
