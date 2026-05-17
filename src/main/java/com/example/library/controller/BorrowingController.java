package com.example.library.controller;

import com.example.library.dto.request.BorrowBookRequest;
import com.example.library.dto.response.BorrowingResponse;
import com.example.library.service.BorrowingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BorrowingController {
    
    private final BorrowingService borrowingService;
    private final com.example.library.repository.UserRepository userRepository;
    
    @PostMapping("/{id}/borrow")
    public ResponseEntity<BorrowingResponse> borrowBook(
            @PathVariable Long id,
            @RequestBody(required = false) BorrowBookRequest request,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        String borrowerName = request != null ? request.getBorrowerName() : null;
        String borrowerContact = request != null ? request.getBorrowerContact() : null;
        String borrowerNotes = request != null ? request.getBorrowerNotes() : null;
        BorrowingResponse borrowing = borrowingService.borrowBook(id, userId, borrowerName, borrowerContact, borrowerNotes);
        return ResponseEntity.ok(borrowing);
    }
    
    @PostMapping("/{id}/return")
    public ResponseEntity<BorrowingResponse> returnBook(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        BorrowingResponse borrowing = borrowingService.returnBook(id, userId);
        return ResponseEntity.ok(borrowing);
    }
    
    @GetMapping("/borrowings/my")
    public ResponseEntity<List<BorrowingResponse>> getMyBorrowings(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<BorrowingResponse> borrowings = borrowingService.getUserBorrowings(userId);
        return ResponseEntity.ok(borrowings);
    }
    
    private Long getUserIdFromAuthentication(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
