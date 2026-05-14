package com.duodot.controller;

import com.duodot.dto.UserDTO;
import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/profile")
    public ServiceResponseBean getProfile() {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = userService.getUserProfile(serviceResponseBean);
        return serviceResponseBean;
    }
    
    @PutMapping("/profile")
    public ServiceResponseBean updateProfile(
            @Valid @RequestBody UserDTO userDTO
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = userService.updateProfile(userDTO, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @PostMapping(value = "/profile/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServiceResponseBean uploadProfilePicture(
            @RequestParam("file") MultipartFile file
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = userService.uploadProfilePicture(file, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @GetMapping("/search")
    public ServiceResponseBean searchUser(
            @RequestParam String username
    ) {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = userService.searchUserByUsername(username, serviceResponseBean);
        return serviceResponseBean;
    }
    
    @DeleteMapping("/account")
    public ServiceResponseBean deleteAccount() {
        ServiceResponseBean serviceResponseBean = new ServiceResponseBean();
        serviceResponseBean = userService.deleteAccount(serviceResponseBean);
        return serviceResponseBean;
    }
}
