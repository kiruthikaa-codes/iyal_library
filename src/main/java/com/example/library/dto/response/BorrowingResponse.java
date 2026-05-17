package com.example.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingResponse {
    
    private Long id;
    private Long userId;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private String bookCoverImageUrl;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;
    private Boolean isReturned;
    private String borrowerName;
    private String borrowerContact;
    private String borrowerNotes;
}
