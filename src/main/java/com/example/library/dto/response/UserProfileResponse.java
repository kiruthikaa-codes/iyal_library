package com.example.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    
    private Long id;
    private String name;
    private Boolean isProfilePublic;
    private LocalDateTime createdAt;
    private List<ReadingStatusResponse> currentlyReading;
    private List<ReadingStatusResponse> recentlyFinished;
    private Long totalBooksRead;
}
