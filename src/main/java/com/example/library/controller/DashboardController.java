package com.example.library.controller;

import com.example.library.dto.response.DashboardResponse;
import com.example.library.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {
    
    private final DashboardService dashboardService;
    private final com.example.library.repository.UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        DashboardResponse dashboard = dashboardService.getUserDashboard(userId);
        return ResponseEntity.ok(dashboard);
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
