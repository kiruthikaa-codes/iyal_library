package com.example.library.service;

import com.example.library.dto.request.UpdateProfileVisibilityRequest;
import com.example.library.dto.response.ReadingStatusResponse;
import com.example.library.dto.response.UserProfileResponse;
import com.example.library.dto.response.UserResponse;
import com.example.library.entity.ReadingStatus;
import com.example.library.entity.User;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.exception.UnauthorizedException;
import com.example.library.repository.ReadingStatusRepository;
import com.example.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final ReadingStatusRepository readingStatusRepository;
    
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserResponse(user);
    }
    
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId, Long requestingUserId) {
        User user = userRepository.findByIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check if profile is public or if user is viewing their own profile
        if (!user.getIsProfilePublic() && !userId.equals(requestingUserId)) {
            throw new UnauthorizedException("This profile is private");
        }
        
        // Get current reading statuses
        List<ReadingStatusResponse> currentlyReading = readingStatusRepository
                .findByUserIdAndStatus(userId, ReadingStatus.Status.READING)
                .stream()
                .map(this::mapToReadingStatusResponse)
                .collect(Collectors.toList());
        
        // Get recently finished books (last 5)
        List<ReadingStatusResponse> recentlyFinished = readingStatusRepository
                .findByUserIdAndStatus(userId, ReadingStatus.Status.FINISHED)
                .stream()
                .limit(5)
                .map(this::mapToReadingStatusResponse)
                .collect(Collectors.toList());
        
        // Count total books finished
        Long totalBooksRead = readingStatusRepository.countByUserIdAndStatus(userId, ReadingStatus.Status.FINISHED);
        
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .isProfilePublic(user.getIsProfilePublic())
                .createdAt(user.getCreatedAt())
                .currentlyReading(currentlyReading)
                .recentlyFinished(recentlyFinished)
                .totalBooksRead(totalBooksRead)
                .build();
    }
    
    @Transactional
    public UserResponse updateProfileVisibility(Long userId, UpdateProfileVisibilityRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setIsProfilePublic(request.getIsPublic());
        User updatedUser = userRepository.save(user);
        
        return mapToUserResponse(updatedUser);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void softDeleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .isProfilePublic(user.getIsProfilePublic())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    private ReadingStatusResponse mapToReadingStatusResponse(ReadingStatus readingStatus) {
        return ReadingStatusResponse.builder()
                .id(readingStatus.getId())
                .userId(readingStatus.getUser().getId())
                .userName(readingStatus.getUser().getName())
                .bookId(readingStatus.getBook().getId())
                .bookTitle(readingStatus.getBook().getTitle())
                .bookCoverImageUrl(readingStatus.getBook().getCoverImageUrl())
                .bookAuthor(readingStatus.getBook().getAuthor())
                .status(readingStatus.getStatus())
                .updatedAt(readingStatus.getUpdatedAt())
                .build();
    }
}
