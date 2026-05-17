package com.example.library.controller;

import com.example.library.dto.request.BookRequest;
import com.example.library.dto.response.BookResponse;
import com.example.library.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookController {
    
    private final BookService bookService;
    private final ObjectMapper objectMapper;
    
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Boolean available
    ) {
        List<BookResponse> books = bookService.searchBooks(title, author, genre, available);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/genres")
    public ResponseEntity<List<String>> getAllGenres() {
        List<String> genres = bookService.getAllGenres();
        return ResponseEntity.ok(genres);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<BookResponse>> getRecentlyAddedBooks() {
        List<BookResponse> books = bookService.getRecentlyAddedBooks();
        return ResponseEntity.ok(books);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> createBook(
            @RequestPart("book") String bookJson,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage
    ) throws IOException {
        BookRequest request = objectMapper.readValue(bookJson, BookRequest.class);
        BookResponse book = bookService.createBook(request, coverImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @RequestPart("book") String bookJson,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage
    ) throws IOException {
        BookRequest request = objectMapper.readValue(bookJson, BookRequest.class);
        BookResponse book = bookService.updateBook(id, request, coverImage);
        return ResponseEntity.ok(book);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) throws IOException {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
