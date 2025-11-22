package org.bibliodigit.repository;

import org.bibliodigit.domain.LoanStatus;
import org.bibliodigit.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    
    @EntityGraph(attributePaths = {"book", "book.author", "book.category"})
    Optional<Stock> findByBookIdAndAvailability(Long bookId, Boolean availability);
    
    @EntityGraph(attributePaths = {"book", "book.author", "book.category", "user"})
    List<Stock> findByUserIdAndStatus(Long userId, LoanStatus status);
    
    @Query("SELECT COUNT(s) FROM Stock s WHERE s.user.id = :userId AND s.status = 'ACTIVE'")
    Long countActiveLoansForUser(@Param("userId") Long userId);
    
    @EntityGraph(attributePaths = {"book", "user"})
    @Query("SELECT s FROM Stock s WHERE s.status = 'ACTIVE' AND s.deliveryDate < :now")
    List<Stock> findOverdueLoans(@Param("now") LocalDateTime now);
    
    @EntityGraph(attributePaths = {"book", "book.author"})
    List<Stock> findByUserId(Long userId);
    
    @EntityGraph(attributePaths = {"book", "book.author", "book.category"})
    List<Stock> findByAvailability(Boolean availability);
}
