package com.example.library.service;

import com.example.library.dto.request.BookRequest;
import com.example.library.dto.response.BookResponse;
import com.example.library.dto.response.BorrowingResponse;
import com.example.library.entity.Book;
import com.example.library.entity.Borrowing;
import com.example.library.exception.BadRequestException;
import com.example.library.exception.ResourceNotFoundException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CloudinaryService cloudinaryService;
    private final BorrowingRepository borrowingRepository;
    
    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return mapToResponse(book);
    }
    
    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String title, String author, String genre, Boolean available) {
        List<Book> books = bookRepository.searchBooks(title, author, genre, available);
        return books.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<String> getAllGenres() {
        return bookRepository.findAllDistinctGenres();
    }
    
    @Transactional(readOnly = true)
    public List<BookResponse> getRecentlyAddedBooks() {
        return bookRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public BookResponse createBook(BookRequest request, MultipartFile coverImage) throws IOException {
        String coverImageUrl = null;
        if (coverImage != null && !coverImage.isEmpty()) {
            coverImageUrl = cloudinaryService.uploadImage(coverImage);
        } else if (request.getCoverImageUrl() != null) {
            coverImageUrl = request.getCoverImageUrl();
        }
        
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .genre(request.getGenre())
                .description(request.getDescription())
                .coverImageUrl(coverImageUrl)
                .totalCount(request.getTotalCount() != null ? request.getTotalCount() : 1)
                .availableCount(request.getTotalCount() != null ? request.getTotalCount() : 1)
                .build();
        
        Book savedBook = bookRepository.save(book);
        return mapToResponse(savedBook);
    }
    
    @Transactional
    public BookResponse updateBook(Long id, BookRequest request, MultipartFile coverImage) throws IOException {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setGenre(request.getGenre());
        book.setDescription(request.getDescription());
        
        if (coverImage != null && !coverImage.isEmpty()) {
            // Delete old image if exists
            if (book.getCoverImageUrl() != null) {
                try {
                    cloudinaryService.deleteImage(book.getCoverImageUrl());
                } catch (IOException e) {
                    // Log error but continue
                }
            }
            String newCoverImageUrl = cloudinaryService.uploadImage(coverImage);
            book.setCoverImageUrl(newCoverImageUrl);
        } else if (request.getCoverImageUrl() != null) {
            book.setCoverImageUrl(request.getCoverImageUrl());
        }
        
        if (request.getTotalCount() != null) {
            int difference = request.getTotalCount() - book.getTotalCount();
            book.setTotalCount(request.getTotalCount());
            book.setAvailableCount(book.getAvailableCount() + difference);
            
            if (book.getAvailableCount() < 0) {
                throw new BadRequestException("Available count cannot be negative");
            }
        }
        
        Book updatedBook = bookRepository.save(book);
        return mapToResponse(updatedBook);
    }
    
    @Transactional
    public void deleteBook(Long id) throws IOException {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        try {
            // Delete cover image if exists
            if (book.getCoverImageUrl() != null) {
                try {
                    cloudinaryService.deleteImage(book.getCoverImageUrl());
                } catch (IOException e) {
                    // Log error but continue with book deletion
                    System.err.println("Failed to delete image from Cloudinary: " + e.getMessage());
                }
            }

            // Delete the book (cascade will delete reading statuses and borrowings)
            bookRepository.delete(book);
            bookRepository.flush();

        } catch (Exception e) {
            System.err.println("Error deleting book: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete book. It may have active references.", e);
        }
    }
    
    private BookResponse mapToResponse(Book book) {
        // Get current borrowing information if book is borrowed
        BorrowingResponse currentBorrowing = null;
        if (book.getAvailableCount() < book.getTotalCount()) {
            Optional<Borrowing> borrowing = borrowingRepository.findByBookIdAndIsReturnedFalse(book.getId())
                    .stream()
                    .findFirst();
            if (borrowing.isPresent()) {
                Borrowing b = borrowing.get();
                currentBorrowing = BorrowingResponse.builder()
                        .id(b.getId())
                        .userId(b.getUser().getId())
                        .userName(b.getUser().getName())
                        .bookId(b.getBook().getId())
                        .bookTitle(b.getBook().getTitle())
                        .borrowedAt(b.getBorrowedAt())
                        .borrowerName(b.getBorrowerName())
                        .borrowerContact(b.getBorrowerContact())
                        .borrowerNotes(b.getBorrowerNotes())
                        .build();
            }
        }

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
                .currentBorrowing(currentBorrowing)
                .build();
    }
}
