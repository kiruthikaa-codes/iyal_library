package com.example.library.repository;

import com.example.library.entity.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {
    
    List<Borrowing> findByUserIdAndIsReturnedFalse(Long userId);

    List<Borrowing> findByUserId(Long userId);

    Optional<Borrowing> findByUserIdAndBookIdAndIsReturnedFalse(Long userId, Long bookId);

    List<Borrowing> findByBookIdAndIsReturnedFalse(Long bookId);

    List<Borrowing> findByIsReturnedFalseOrderByBorrowedAtDesc();
    
    Long countByIsReturnedFalse();
    
    @Query("SELECT b.book.id, COUNT(b) as borrowCount " +
           "FROM Borrowing b " +
           "GROUP BY b.book.id " +
           "ORDER BY borrowCount DESC")
    List<Object[]> findMostBorrowedBooks();
    
    @Query("SELECT b FROM Borrowing b " +
           "WHERE b.user.id = :userId " +
           "ORDER BY b.borrowedAt DESC")
    List<Borrowing> findByUserIdOrderByBorrowedAtDesc(@Param("userId") Long userId);
}
