package com.duodot.service;

import com.duodot.dto.UserDTO;
import com.duodot.responseBean.ServiceResponseBean;
import com.duodot.responseBean.UserResponseBean;
import com.duodot.entity.User;
import com.duodot.repository.UserRepository;
import com.duodot.requestBean.UserRequestBean;

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
        User principal = (User) authentication.getPrincipal();
        return userRepository.findByUserId(principal.getUserId());
    }

    public UserDTO toUserDTO(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setPaired(user.getPairIds() != null && !user.getPairIds().isEmpty());
        return dto;
    }

    public ServiceResponseBean getUserProfile(ServiceResponseBean serviceResponseBean) {
        try {
            User user = getCurrentUser();
            UserResponseBean userResponse = new UserResponseBean();
            userResponse.setUserId(user.getUserId());
            userResponse.setUsername(user.getUsername());
            userResponse.setFirstname(user.getFirstName());
            userResponse.setLastname(user.getLastName());
            userResponse.setEmail(user.getEmail());
            userResponse.setCountrycode(user.getCountryCode());
            userResponse.setPhoneNumber(user.getPhoneNumber());
            userResponse.setProfilePicture(user.getProfilePicture());
            userResponse.setCity(user.getCity());
            userResponse.setIsPaired(user.getPairIds() != null && !user.getPairIds().isEmpty());
            userResponse.setIsDeleted(user.getIsDeleted());
            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("Profile retrieved successfully");
            serviceResponseBean.setData(userResponse);
        } catch (Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }

    public ServiceResponseBean searchUserByUsername(String username, ServiceResponseBean serviceResponseBean) {
        try {
            log.info("Searching user by username={}", username);
            Optional<User> userOpt = userRepository.findByUsernameAndIsDeletedFalse(username);
           
            if (userOpt.isEmpty()) {
                serviceResponseBean.setMessage("User not found with username: " + username);
                return serviceResponseBean;
            }
            User foundUser = userOpt.get();
            UserResponseBean userResponseBean = new UserResponseBean();
            userResponseBean.setUserId(foundUser.getUserId());
            userResponseBean.setUsername(foundUser.getUsername());
            userResponseBean.setFirstname(foundUser.getFirstName());
            userResponseBean.setLastname(foundUser.getLastName());
            serviceResponseBean.setStatus(Boolean.TRUE);
            serviceResponseBean.setMessage("User found");
            serviceResponseBean.setData(userResponseBean);
        } catch (Exception e) {
            log.error("Exception occurred :: {}", e);
            serviceResponseBean.setMessage(e.getLocalizedMessage());
        }
        return serviceResponseBean;
    }

    @Transactional
    public ServiceResponseBean updateProfile(UserRequestBean userRequestBean, ServiceResponseBean serviceResponseBean) {
        try {
            log.info("Update profile request={}", userRequestBean);
            User user = getCurrentUser();
            user.setFirstName(userRequestBean.getFirstName() != null ? userRequestBean.getFirstName() : user.getFirstName());
            user.setLastName(userRequestBean.getLastName() != null ? userRequestBean.getLastName() : user.getLastName());
            user.setPhoneNumber(userRequestBean.getPhoneNumber() != null ? userRequestBean.getPhoneNumber() : user.getPhoneNumber());
            user.setCity(userRequestBean.getCity() != null ? userRequestBean.getCity() : user.getCity());
            user.setState(userRequestBean.getState() != null ? userRequestBean.getState() : user.getState());
            userRepository.save(user);
            this.getUserProfile(serviceResponseBean);
            serviceResponseBean.setMessage("Profile updated successfully");
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
            this.getUserProfile(serviceResponseBean);
            serviceResponseBean.setMessage("Profile picture uploaded successfully");
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
