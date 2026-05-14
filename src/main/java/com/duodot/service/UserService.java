package com.duodot.service;

import com.duodot.dto.UserDTO;
import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.entity.User;
import com.duodot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public UserDTO toUserDTO(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setPaired(user.getPairIds() != null && !user.getPairIds().isEmpty());
        return dto;
    }

    public ServiceResponseBean getUserProfile(ServiceResponseBean serviceResponseBean) {
        try {
            User user = getCurrentUser();
            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("Profile retrieved successfully");
            serviceResponseBean.setData(toUserDTO(user));
        } catch (Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }

    public ServiceResponseBean searchUserByUsername(String username, ServiceResponseBean serviceResponseBean) {
        try {
            log.info("Searching user by username={}", username);
            Optional<User> userOpt = userRepository.findByUserIdAndIsDeletedFalse(username);
            if (userOpt.isEmpty()) {
                serviceResponseBean.setMessage("User not found with username: " + username);
                return serviceResponseBean;
            }
            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("User found");
            serviceResponseBean.setData(toUserDTO(userOpt.get()));
        } catch (Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }

    @Transactional
    public ServiceResponseBean updateProfile(UserDTO userDTO, ServiceResponseBean serviceResponseBean) {
        try {
            log.info("Update profile request={}", userDTO);
            User user = getCurrentUser();
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setCity(userDTO.getCity());
            user.setState(userDTO.getState());
            userRepository.save(user);
            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("Profile updated successfully");
            serviceResponseBean.setData(toUserDTO(user));
        } catch (Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }

    @Transactional
    public ServiceResponseBean uploadProfilePicture(MultipartFile file, ServiceResponseBean serviceResponseBean) {
        try {
            User user = getCurrentUser();
            String fileUrl = fileStorageService.uploadFile(file, "profile-pictures");
            user.setProfilePicture(fileUrl);
            userRepository.save(user);
            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("Profile picture uploaded successfully");
            serviceResponseBean.setData(toUserDTO(user));
        } catch (Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }

    @Transactional
    public ServiceResponseBean deleteAccount(ServiceResponseBean serviceResponseBean) {
        try {
            User user = getCurrentUser();
            user.setIsDeleted(true);
            userRepository.save(user);
            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("Account deleted successfully");
        } catch (Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }
}
