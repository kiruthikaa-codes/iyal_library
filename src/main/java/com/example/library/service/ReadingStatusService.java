package com.example.library.service;

import com.example.library.dto.request.ReadingStatusRequest;
import com.example.library.dto.response.ReadingStatusResponse;
import com.example.library.entity.Book;
import com.example.library.entity.ReadingStatus;
import com.example.library.entity.User;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.ReadingStatusRepository;
import com.example.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadingStatusService {
    
    private final ReadingStatusRepository readingStatusRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public ReadingStatusResponse setReadingStatus(Long bookId, Long userId, ReadingStatusRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        
        // Check if reading status already exists
        ReadingStatus readingStatus = readingStatusRepository.findByUserIdAndBookId(userId, bookId)
                .orElse(ReadingStatus.builder()
                        .user(user)
                        .book(book)
                        .build());
        
        readingStatus.setStatus(request.getStatus());
        ReadingStatus savedStatus = readingStatusRepository.save(readingStatus);
        
        return mapToResponse(savedStatus);
    }
    
    @Transactional(readOnly = true)
    public List<ReadingStatusResponse> getUserReadingStatuses(Long userId) {
        return readingStatusRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReadingStatusResponse> getUserReadingStatusesByStatus(Long userId, ReadingStatus.Status status) {
        return readingStatusRepository.findByUserIdAndStatus(userId, status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReadingStatusResponse> getCurrentlyReadingByFamily() {
        return readingStatusRepository.findPublicReadingStatusesByStatus(ReadingStatus.Status.READING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ReadingStatusResponse getUserBookReadingStatus(Long userId, Long bookId) {
        ReadingStatus readingStatus = readingStatusRepository.findByUserIdAndBookId(userId, bookId)
                .orElse(null);

        if (readingStatus == null) {
            return null;
        }

        return mapToResponse(readingStatus);
    }

    @Transactional
    public void removeReadingStatus(Long bookId, Long userId) {
        ReadingStatus readingStatus = readingStatusRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading status not found"));

        readingStatusRepository.delete(readingStatus);
    }

    private ReadingStatusResponse mapToResponse(ReadingStatus readingStatus) {
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
}
