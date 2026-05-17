package com.example.library.controller;

import com.example.library.dto.request.UpdateProfileVisibilityRequest;
import com.example.library.dto.response.UserProfileResponse;
import com.example.library.dto.response.UserResponse;
import com.example.library.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    private final com.example.library.repository.UserRepository userRepository;
    
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserResponse user = userService.getCurrentUser(userId);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long requestingUserId = getUserIdFromAuthentication(authentication);
        UserProfileResponse profile = userService.getUserProfile(id, requestingUserId);
        return ResponseEntity.ok(profile);
    }
    
    @PatchMapping("/profile-visibility")
    public ResponseEntity<UserResponse> updateProfileVisibility(
            @Valid @RequestBody UpdateProfileVisibilityRequest request,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        UserResponse user = userService.updateProfileVisibility(userId, request);
        return ResponseEntity.ok(user);
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
