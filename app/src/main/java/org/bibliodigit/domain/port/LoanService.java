package org.bibliodigit.domain.port;

import org.bibliodigit.domain.Stock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LoanService {
    
    Stock borrowBook(Long userId, Long bookId);
    
    Stock returnBook(Long stockId);
    
    List<Stock> getActiveLoansForUser(Long userId);
    List<Stock> getLoanHistoryForUser(Long userId);
    List<Stock> getOverdueLoans();
    Optional<Stock> findById(Long id);
    
    boolean canUserBorrow(Long userId);
    Long countActiveLoansForUser(Long userId);
    
    BigDecimal calculateFine(Long stockId);
}
