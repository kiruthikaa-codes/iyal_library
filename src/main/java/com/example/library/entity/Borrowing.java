package com.example.library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "borrowings")
@Data
@EqualsAndHashCode(exclude = {"user", "book"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Borrowing {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @CreationTimestamp
    @Column(name = "borrowed_at", nullable = false, updatable = false)
    private LocalDateTime borrowedAt;
    
    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(name = "is_returned", nullable = false)
    @Builder.Default
    private Boolean isReturned = false;

    @Column(name = "borrower_name")
    private String borrowerName;

    @Column(name = "borrower_contact")
    private String borrowerContact;

    @Column(name = "borrower_notes", columnDefinition = "TEXT")
    private String borrowerNotes;
}
