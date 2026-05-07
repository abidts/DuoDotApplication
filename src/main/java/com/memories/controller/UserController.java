package com.memories.controller;

import com.memories.dto.ApiResponse;
import com.memories.dto.UserDTO;
import com.memories.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile() {
        UserDTO user = userService.getUserProfile();
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", user));
    }
    
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(
            @Valid @RequestBody UserDTO userDTO
    ) {
        UserDTO updatedUser = userService.updateProfile(userDTO);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedUser));
    }
    
    @PostMapping(value = "/profile/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserDTO>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file
    ) {
        UserDTO user = userService.uploadProfilePicture(file);
        return ResponseEntity.ok(ApiResponse.success("Profile picture uploaded successfully", user));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserDTO>> searchUser(
            @RequestParam String username
    ) {
        UserDTO user = userService.searchUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("User found", user));
    }
    
    @DeleteMapping("/account")
    public ResponseEntity<ApiResponse<Void>> deleteAccount() {
        userService.deleteAccount();
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }
}
