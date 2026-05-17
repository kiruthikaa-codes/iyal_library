package com.example.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Data
@EqualsAndHashCode(exclude = {"readingStatuses", "borrowings"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(min = 1, max = 200, message = "Author must be between 1 and 200 characters")
    @Column(nullable = false, length = 200)
    private String author;
    
    @NotBlank(message = "Genre is required")
    @Size(min = 1, max = 100, message = "Genre must be between 1 and 100 characters")
    @Column(nullable = false, length = 100)
    private String genre;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;
    
    @Min(value = 1, message = "Total count must be at least 1")
    @Column(name = "total_count", nullable = false)
    @Builder.Default
    private Integer totalCount = 1;
    
    @Min(value = 0, message = "Available count cannot be negative")
    @Column(name = "available_count", nullable = false)
    @Builder.Default
    private Integer availableCount = 1;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ReadingStatus> readingStatuses = new HashSet<>();
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Borrowing> borrowings = new HashSet<>();
}
