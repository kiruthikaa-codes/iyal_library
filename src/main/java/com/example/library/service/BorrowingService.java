package com.example.library.service;

import com.example.library.dto.response.BorrowingResponse;
import com.example.library.entity.Book;
import com.example.library.entity.Borrowing;
import com.example.library.entity.User;
import com.example.library.exception.BadRequestException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowingRepository;
import com.example.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowingService {
    
    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public BorrowingResponse borrowBook(Long bookId, Long userId, String borrowerName, String borrowerContact, String borrowerNotes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        // Check if book is available
        if (book.getAvailableCount() <= 0) {
            throw new BadRequestException("Book is not available for borrowing");
        }

        // Check if user already has this book borrowed
        if (borrowingRepository.findByUserIdAndBookIdAndIsReturnedFalse(userId, bookId).isPresent()) {
            throw new BadRequestException("You have already borrowed this book");
        }

        // Create borrowing record with borrower information
        Borrowing borrowing = Borrowing.builder()
                .user(user)
                .book(book)
                .isReturned(false)
                .borrowerName(borrowerName)
                .borrowerContact(borrowerContact)
                .borrowerNotes(borrowerNotes)
                .build();

        Borrowing savedBorrowing = borrowingRepository.save(borrowing);

        // Update book available count
        book.setAvailableCount(book.getAvailableCount() - 1);
        bookRepository.save(book);

        return mapToResponse(savedBorrowing);
    }
    
    @Transactional
    public BorrowingResponse returnBook(Long bookId, Long userId) {
        Borrowing borrowing = borrowingRepository.findByUserIdAndBookIdAndIsReturnedFalse(userId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException("No active borrowing found for this book"));
        
        // Mark as returned
        borrowing.setIsReturned(true);
        borrowing.setReturnedAt(LocalDateTime.now());
        Borrowing updatedBorrowing = borrowingRepository.save(borrowing);
        
        // Update book available count
        Book book = borrowing.getBook();
        book.setAvailableCount(book.getAvailableCount() + 1);
        bookRepository.save(book);
        
        return mapToResponse(updatedBorrowing);
    }
    
    @Transactional(readOnly = true)
    public List<BorrowingResponse> getUserBorrowings(Long userId) {
        return borrowingRepository.findByUserIdOrderByBorrowedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BorrowingResponse> getCurrentlyBorrowedBooks() {
        return borrowingRepository.findByIsReturnedFalseOrderByBorrowedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private BorrowingResponse mapToResponse(Borrowing borrowing) {
        return BorrowingResponse.builder()
                .id(borrowing.getId())
                .userId(borrowing.getUser().getId())
                .userName(borrowing.getUser().getName())
                .bookId(borrowing.getBook().getId())
                .bookTitle(borrowing.getBook().getTitle())
                .bookCoverImageUrl(borrowing.getBook().getCoverImageUrl())
                .borrowedAt(borrowing.getBorrowedAt())
                .returnedAt(borrowing.getReturnedAt())
                .isReturned(borrowing.getIsReturned())
                .borrowerName(borrowing.getBorrowerName())
                .borrowerContact(borrowing.getBorrowerContact())
                .borrowerNotes(borrowing.getBorrowerNotes())
                .build();
    }
}
