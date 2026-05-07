package com.duodot.service;

import com.duodot.dto.UserDTO;
import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.entity.User;
import com.duodot.exception.ResourceNotFoundException;
import com.duodot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);

    }
    
    public ServiceResponseBean getUserProfile(ServiceResponseBean serviceResponseBean) {
        User user = getCurrentUser();
        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Profile retrieved successfully");
        serviceResponseBean.setData(modelMapper.map(user, UserDTO.class));
        return serviceResponseBean;
    }
    
    public ServiceResponseBean searchUserByUsername(String username, ServiceResponseBean serviceResponseBean) {
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("User found");
        serviceResponseBean.setData(modelMapper.map(user, UserDTO.class));
        return serviceResponseBean;
    }
    
    @Transactional
    public ServiceResponseBean updateProfile(UserDTO userDTO, ServiceResponseBean serviceResponseBean) {
        User user = getCurrentUser();
        
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setCity(userDTO.getCity());
        user.setState(userDTO.getState());
        
        userRepository.save(user);
        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Profile updated successfully");
        serviceResponseBean.setData(modelMapper.map(user, UserDTO.class));
        return serviceResponseBean;
    }
    
    @Transactional
    public ServiceResponseBean uploadProfilePicture(MultipartFile file, ServiceResponseBean serviceResponseBean) {
        User user = getCurrentUser();
        String fileUrl = fileStorageService.uploadFile(file, "profile-pictures");
        user.setProfilePicture(fileUrl);
        userRepository.save(user);
        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Profile picture uploaded successfully");
        serviceResponseBean.setData(modelMapper.map(user, UserDTO.class));
        return serviceResponseBean;
    }
    
    @Transactional
    public ServiceResponseBean deleteAccount(ServiceResponseBean serviceResponseBean) {
        User user = getCurrentUser();
        user.setIsDeleted(true);
        userRepository.save(user);
        serviceResponseBean.setStatus(Boolean.TRUE);
        serviceResponseBean.setMessage("Account deleted successfully");
        serviceResponseBean.setData(null);
        return serviceResponseBean;
    }
}
