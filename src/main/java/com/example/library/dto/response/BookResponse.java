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
public class BookResponse {
    
    private Long id;
    private String title;
    private String author;
    private String genre;
    private String description;
    private String coverImageUrl;
    private Integer totalCount;
    private Integer availableCount;
    private LocalDateTime createdAt;
    private Boolean isAvailable;
    private BorrowingResponse currentBorrowing;

    public Boolean getIsAvailable() {
        return availableCount != null && availableCount > 0;
    }
}
