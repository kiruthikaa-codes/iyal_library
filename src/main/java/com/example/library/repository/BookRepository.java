package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    List<Book> findByGenreIgnoreCase(String genre);
    
    List<Book> findByAvailableCountGreaterThan(Integer count);
    
    @Query("SELECT DISTINCT b.genre FROM Book b ORDER BY b.genre")
    List<String> findAllDistinctGenres();
    
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:genre IS NULL OR LOWER(b.genre) = LOWER(:genre)) AND " +
           "(:available IS NULL OR (:available = true AND b.availableCount > 0) OR (:available = false))")
    List<Book> searchBooks(@Param("title") String title, 
                          @Param("author") String author, 
                          @Param("genre") String genre, 
                          @Param("available") Boolean available);
    
    List<Book> findTop10ByOrderByCreatedAtDesc();
}
