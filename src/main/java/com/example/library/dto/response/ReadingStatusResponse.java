package com.example.library.dto.response;

import com.example.library.entity.ReadingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingStatusResponse {
    
    private Long id;
    private Long userId;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private String bookCoverImageUrl;
    private String bookAuthor;
    private ReadingStatus.Status status;
    private LocalDateTime updatedAt;
}
