package org.bibliodigit.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bibliodigit.api.dto.req.LoanRequest;
import org.bibliodigit.api.dto.res.LoanResponse;
import org.bibliodigit.api.mapper.LoanMapper;
import org.bibliodigit.domain.Stock;
import org.bibliodigit.domain.port.LoanService;
import org.bibliodigit.security.RequireRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final LoanMapper mapper;

    @PostMapping
    @RequireRole({"ADMIN", "STUDENT", "TEACHER", "EXTERNAL"})
    public ResponseEntity<LoanResponse> borrowBook(@Valid @RequestBody LoanRequest request) {
        log.debug("Loan request: userId={}, bookId={}", request.getUserId(), request.getBookId());

        try {
            Stock loan = loanService.borrowBook(request.getUserId(), request.getBookId());
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toResponse(loan));
        } catch (RuntimeException e) {
            log.error("Error borrowing book: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/return")
    @RequireRole({"ADMIN", "STUDENT", "TEACHER", "EXTERNAL"})
    public ResponseEntity<LoanResponse> returnBook(@PathVariable Long id) {
        log.debug("Return request for loan: {}", id);

        try {
            Stock returned = loanService.returnBook(id);
            return ResponseEntity.ok(mapper.toResponse(returned));
        } catch (RuntimeException e) {
            log.error("Error returning book: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/active")
    @RequireRole({"ADMIN", "STUDENT", "TEACHER", "EXTERNAL"})
    public ResponseEntity<List<LoanResponse>> getActiveLoans(@PathVariable Long userId) {
        log.debug("Getting active loans for user: {}", userId);

        List<LoanResponse> responses = loanService.getActiveLoansForUser(userId)
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}/history")
    @RequireRole({"ADMIN", "STUDENT", "TEACHER", "EXTERNAL"})
    public ResponseEntity<List<LoanResponse>> getLoanHistory(@PathVariable Long userId) {
        log.debug("Getting loan history for user: {}", userId);

        List<LoanResponse> responses = loanService.getLoanHistoryForUser(userId)
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }

    @GetMapping("/overdue")
    @RequireRole("ADMIN")
    public ResponseEntity<List<LoanResponse>> getOverdueLoans() {
        log.debug("Getting overdue loans");

        List<LoanResponse> responses = loanService.getOverdueLoans()
            .stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return responses.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/fine")
    @RequireRole({"ADMIN", "STUDENT", "TEACHER", "EXTERNAL"})
    public ResponseEntity<BigDecimal> calculateFine(@PathVariable Long id) {
        log.debug("Calculating fine for loan: {}", id);

        try {
            BigDecimal fine = loanService.calculateFine(id);
            return ResponseEntity.ok(fine);
        } catch (RuntimeException e) {
            log.error("Error calculating fine: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/can-borrow")
    @RequireRole({"ADMIN", "STUDENT", "TEACHER", "EXTERNAL"})
    public ResponseEntity<Boolean> canUserBorrow(@PathVariable Long userId) {
        log.debug("Checking if user {} can borrow", userId);

        boolean canBorrow = loanService.canUserBorrow(userId);
        return ResponseEntity.ok(canBorrow);
    }


    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "STUDENT", "TEACHER", "EXTERNAL"})
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable Long id) {
        log.debug("Getting loan by id: {}", id);

        return loanService.findById(id)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
