package com.example.library.repository;

import com.example.library.entity.ReadingStatus;
import com.example.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingStatusRepository extends JpaRepository<ReadingStatus, Long> {
    
    Optional<ReadingStatus> findByUserIdAndBookId(Long userId, Long bookId);
    
    List<ReadingStatus> findByUserIdAndStatus(Long userId, ReadingStatus.Status status);
    
    List<ReadingStatus> findByUserId(Long userId);
    
    @Query("SELECT rs FROM ReadingStatus rs " +
           "WHERE rs.user.isProfilePublic = true " +
           "AND rs.user.isActive = true " +
           "AND rs.status = :status " +
           "ORDER BY rs.updatedAt DESC")
    List<ReadingStatus> findPublicReadingStatusesByStatus(@Param("status") ReadingStatus.Status status);
    
    @Query("SELECT rs FROM ReadingStatus rs " +
           "WHERE rs.user.id = :userId " +
           "ORDER BY rs.updatedAt DESC")
    List<ReadingStatus> findByUserIdOrderByUpdatedAtDesc(@Param("userId") Long userId);
    
    Long countByUserIdAndStatus(Long userId, ReadingStatus.Status status);
}
