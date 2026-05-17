package com.example.library.controller;

import com.example.library.dto.request.ReadingStatusRequest;
import com.example.library.dto.response.ReadingStatusResponse;
import com.example.library.entity.ReadingStatus;
import com.example.library.service.ReadingStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReadingStatusController {
    
    private final ReadingStatusService readingStatusService;
    private final com.example.library.repository.UserRepository userRepository;
    
    @PostMapping("/{id}/status")
    public ResponseEntity<ReadingStatusResponse> setReadingStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReadingStatusRequest request,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        ReadingStatusResponse status = readingStatusService.setReadingStatus(id, userId, request);
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/{id}/status/my")
    public ResponseEntity<ReadingStatusResponse> getMyReadingStatus(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        ReadingStatusResponse status = readingStatusService.getUserBookReadingStatus(userId, id);
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/reading-status/my")
    public ResponseEntity<List<ReadingStatusResponse>> getMyReadingStatuses(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<ReadingStatusResponse> statuses = readingStatusService.getUserReadingStatuses(userId);
        return ResponseEntity.ok(statuses);
    }
    
    @GetMapping("/reading-status/currently-reading")
    public ResponseEntity<List<ReadingStatusResponse>> getCurrentlyReadingByFamily() {
        List<ReadingStatusResponse> statuses = readingStatusService.getCurrentlyReadingByFamily();
        return ResponseEntity.ok(statuses);
    }
    
    @GetMapping("/reading-status/filter")
    public ResponseEntity<List<ReadingStatusResponse>> getReadingStatusesByStatus(
            @RequestParam ReadingStatus.Status status,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<ReadingStatusResponse> statuses = readingStatusService.getUserReadingStatusesByStatus(userId, status);
        return ResponseEntity.ok(statuses);
    }

    @DeleteMapping("/{id}/status")
    public ResponseEntity<Void> removeReadingStatus(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        readingStatusService.removeReadingStatus(id, userId);
        return ResponseEntity.ok().build();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
