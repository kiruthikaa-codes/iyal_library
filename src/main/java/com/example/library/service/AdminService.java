package com.example.library.service;

import com.example.library.dto.response.AdminAnalyticsResponse;
import com.example.library.entity.Book;
import com.example.library.entity.ReadingStatus;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowingRepository;
import com.example.library.repository.ReadingStatusRepository;
import com.example.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowingRepository borrowingRepository;
    private final ReadingStatusRepository readingStatusRepository;
    
    @Transactional(readOnly = true)
    public AdminAnalyticsResponse getAnalytics() {
        // Total books
        Long totalBooks = bookRepository.count();
        
        // Total users
        Long totalUsers = userRepository.count();
        
        // Currently borrowed books
        Long currentlyBorrowedBooks = borrowingRepository.countByIsReturnedFalse();
        
        // Active users
        Long activeUsers = (long) userRepository.findByIsActiveTrue().size();
        
        // Popular genres (books count by genre)
        List<Book> allBooks = bookRepository.findAll();
        Map<String, Long> popularGenres = allBooks.stream()
                .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()));
        
        // Most read books (by finished reading status count)
        List<ReadingStatus> finishedStatuses = readingStatusRepository.findAll().stream()
                .filter(rs -> rs.getStatus() == ReadingStatus.Status.FINISHED)
                .collect(Collectors.toList());
        
        Map<String, Long> mostReadBooks = finishedStatuses.stream()
                .collect(Collectors.groupingBy(
                        rs -> rs.getBook().getTitle(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        HashMap::new
                ));
        
        return AdminAnalyticsResponse.builder()
                .totalBooks(totalBooks)
                .totalUsers(totalUsers)
                .currentlyBorrowedBooks(currentlyBorrowedBooks)
                .activeUsers(activeUsers)
                .popularGenres(popularGenres)
                .mostReadBooks(mostReadBooks)
                .build();
    }
}
