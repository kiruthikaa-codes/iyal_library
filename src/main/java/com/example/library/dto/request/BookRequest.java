package com.example.library.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(min = 1, max = 200, message = "Author must be between 1 and 200 characters")
    private String author;
    
    @NotBlank(message = "Genre is required")
    @Size(min = 1, max = 100, message = "Genre must be between 1 and 100 characters")
    private String genre;
    
    private String description;
    
    private String coverImageUrl;
    
    @Min(value = 1, message = "Total count must be at least 1")
    private Integer totalCount;
}
