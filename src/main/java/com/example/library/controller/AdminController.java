package com.example.library.controller;

import com.example.library.dto.response.AdminAnalyticsResponse;
import com.example.library.dto.response.BorrowingResponse;
import com.example.library.dto.response.UserResponse;
import com.example.library.service.AdminService;
import com.example.library.service.BorrowingService;
import com.example.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {
    
    private final UserService userService;
    private final BorrowingService borrowingService;
    private final AdminService adminService;
    
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/borrowings")
    public ResponseEntity<List<BorrowingResponse>> getCurrentlyBorrowedBooks() {
        List<BorrowingResponse> borrowings = borrowingService.getCurrentlyBorrowedBooks();
        return ResponseEntity.ok(borrowings);
    }
    
    @GetMapping("/analytics")
    public ResponseEntity<AdminAnalyticsResponse> getAnalytics() {
        AdminAnalyticsResponse analytics = adminService.getAnalytics();
        return ResponseEntity.ok(analytics);
    }
}
