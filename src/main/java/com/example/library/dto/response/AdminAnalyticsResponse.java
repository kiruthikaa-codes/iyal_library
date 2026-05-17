package com.example.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAnalyticsResponse {
    
    private Long totalBooks;
    private Long totalUsers;
    private Long currentlyBorrowedBooks;
    private Long activeUsers;
    private Map<String, Long> popularGenres;
    private Map<String, Long> mostReadBooks;
}
