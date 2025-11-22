package org.bibliodigit.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bibliodigit.domain.Book;
import org.bibliodigit.domain.LoanStatus;
import org.bibliodigit.domain.Stock;
import org.bibliodigit.domain.User;
import org.bibliodigit.domain.port.LoanService;
import org.bibliodigit.domain.strategy.LoanValidationStrategy;
import org.bibliodigit.domain.strategy.LoanValidatorFactory;
import org.bibliodigit.repository.BookRepository;
import org.bibliodigit.repository.StockRepository;
import org.bibliodigit.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor  
@Transactional(readOnly = true)
public class LoanServiceImpl implements LoanService {

    
    private final StockRepository stockRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanValidatorFactory validatorFactory;


    @Override
    @Transactional
    public Stock borrowBook(Long userId, Long bookId) {
        log.debug("Borrowing book {} for user {}", bookId, userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (!user.getIsActive()) {
            throw new RuntimeException("User account is inactive");
        }

        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

        Stock stock = stockRepository.findByBookIdAndAvailability(bookId, true)
            .orElseThrow(() -> new RuntimeException("Book is not available for loan"));

        Long currentLoans = stockRepository.countActiveLoansForUser(userId);

        String userType = user.getTypeUser().getType();
        LoanValidationStrategy validator = validatorFactory.getValidator(userType);

        if (!validator.canBorrow(user, currentLoans.intValue())) {
            throw new RuntimeException(
                String.format("User cannot borrow more books. Max: %d, Current: %d", 
                    validator.getMaxBooks(), currentLoans)
            );
        }

        LocalDateTime now = LocalDateTime.now();
        stock.setAvailability(false);
        stock.setUser(user);
        stock.setDepartureDate(now);
        stock.setDeliveryDate(now.plusDays(validator.getMaxDays()));
        stock.setStatus(LoanStatus.ACTIVE);

        Stock saved = stockRepository.save(stock);

        log.debug("Book {} borrowed successfully by user {}", bookId, userId);
        return saved;
    }


    @Override
    @Transactional
    public Stock returnBook(Long stockId) {
        log.debug("Returning book with stock id: {}", stockId);

        Stock stock = stockRepository.findById(stockId)
            .orElseThrow(() -> new RuntimeException("Loan not found with id: " + stockId));

        if (stock.getStatus() != LoanStatus.ACTIVE) {
            throw new RuntimeException("Loan is not active. Current status: " + stock.getStatus());
        }

        LocalDateTime now = LocalDateTime.now();
        stock.setActualReturnDate(now);
        stock.setAvailability(true);
        stock.setUser(null);

        if (stock.isOverdue()) {
            stock.setStatus(LoanStatus.OVERDUE);
            BigDecimal fine = stock.calculateFine();
            stock.setFine(fine);
            log.warn("Book returned late. Fine: ${}", fine);
        } else {
            stock.setStatus(LoanStatus.RETURNED);
            stock.setFine(BigDecimal.ZERO);
        }

        Stock saved = stockRepository.save(stock);

        log.debug("Book returned successfully. Status: {}, Fine: ${}", 
            saved.getStatus(), saved.getFine());
        return saved;
    }


    @Override
    public List<Stock> getActiveLoansForUser(Long userId) {
        log.debug("Getting active loans for user: {}", userId);
        return stockRepository.findByUserIdAndStatus(userId, LoanStatus.ACTIVE);
    }

    @Override
    public List<Stock> getLoanHistoryForUser(Long userId) {
        log.debug("Getting loan history for user: {}", userId);
        return stockRepository.findByUserId(userId);
    }

    @Override
    public List<Stock> getOverdueLoans() {
        log.debug("Getting overdue loans");
        return stockRepository.findOverdueLoans(LocalDateTime.now());
    }

    @Override
    public Optional<Stock> findById(Long id) {
        return stockRepository.findById(id);
    }


    @Override
    public boolean canUserBorrow(Long userId) {
        User user = userRepository.findById(userId)
            .orElse(null);

        if (user == null || !user.getIsActive()) {
            return false;
        }

        Long currentLoans = stockRepository.countActiveLoansForUser(userId);
        LoanValidationStrategy validator = validatorFactory.getValidator(user.getTypeUser().getType());

        return validator.canBorrow(user, currentLoans.intValue());
    }

    @Override
    public Long countActiveLoansForUser(Long userId) {
        return stockRepository.countActiveLoansForUser(userId);
    }


    @Override
    public BigDecimal calculateFine(Long stockId) {
        Stock stock = stockRepository.findById(stockId)
            .orElseThrow(() -> new RuntimeException("Loan not found"));

        return stock.calculateFine();
    }
}
