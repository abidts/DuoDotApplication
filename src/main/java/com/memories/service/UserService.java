package com.memories.service;

import com.memories.dto.UserDTO;
import com.memories.entity.User;
import com.memories.exception.ResourceNotFoundException;
import com.memories.repository.UserRepository;
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
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    public UserDTO getUserProfile() {
        User user = getCurrentUser();
        return modelMapper.map(user, UserDTO.class);
    }
    
    public UserDTO searchUserByUsername(String username) {
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return modelMapper.map(user, UserDTO.class);
    }
    
    @Transactional
    public UserDTO updateProfile(UserDTO userDTO) {
        User user = getCurrentUser();
        
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setCity(userDTO.getCity());
        user.setState(userDTO.getState());
        user.setPinId(userDTO.getPinId());
        
        userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }
    
    @Transactional
    public UserDTO uploadProfilePicture(MultipartFile file) {
        User user = getCurrentUser();
        String fileUrl = fileStorageService.uploadFile(file, "profile-pictures");
        user.setProfilePicture(fileUrl);
        userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }
    
    @Transactional
    public void deleteAccount() {
        User user = getCurrentUser();
        user.setDeleted(true);
        userRepository.save(user);
    }
}
