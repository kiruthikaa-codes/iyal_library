package com.example.library.service;

import com.example.library.dto.response.BookResponse;
import com.example.library.dto.response.DashboardResponse;
import com.example.library.dto.response.ReadingStatusResponse;
import com.example.library.entity.ReadingStatus;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowingRepository;
import com.example.library.repository.ReadingStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final ReadingStatusRepository readingStatusRepository;
    private final BookRepository bookRepository;
    private final BorrowingRepository borrowingRepository;
    
    @Transactional(readOnly = true)
    public DashboardResponse getUserDashboard(Long userId) {
        // Continue Reading (user's currently reading books)
        List<ReadingStatusResponse> continueReading = readingStatusRepository
                .findByUserIdAndStatus(userId, ReadingStatus.Status.READING)
                .stream()
                .map(this::mapToReadingStatusResponse)
                .limit(5)
                .collect(Collectors.toList());
        
        // Currently Reading by Family (public profiles reading)
        List<ReadingStatusResponse> currentlyReadingByFamily = readingStatusRepository
                .findPublicReadingStatusesByStatus(ReadingStatus.Status.READING)
                .stream()
                .filter(rs -> !rs.getUser().getId().equals(userId)) // Exclude current user
                .limit(10)
                .map(this::mapToReadingStatusResponse)
                .collect(Collectors.toList());
        
        // Recently Added Books
        List<BookResponse> recentlyAdded = bookRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
        
        // Popular Books (most borrowed)
        List<Object[]> mostBorrowed = borrowingRepository.findMostBorrowedBooks();
        List<Long> popularBookIds = mostBorrowed.stream()
                .limit(10)
                .map(row -> (Long) row[0])
                .collect(Collectors.toList());
        
        List<BookResponse> popularBooks = bookRepository.findAllById(popularBookIds)
                .stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
        
        return DashboardResponse.builder()
                .continueReading(continueReading)
                .currentlyReadingByFamily(currentlyReadingByFamily)
                .recentlyAdded(recentlyAdded)
                .popularBooks(popularBooks)
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
    
    private BookResponse mapToBookResponse(com.example.library.entity.Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .genre(book.getGenre())
                .description(book.getDescription())
                .coverImageUrl(book.getCoverImageUrl())
                .totalCount(book.getTotalCount())
                .availableCount(book.getAvailableCount())
                .createdAt(book.getCreatedAt())
                .isAvailable(book.getAvailableCount() > 0)
                .build();
    }
}
