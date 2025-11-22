package org.bibliodigit.domain.strategy;

import org.bibliodigit.domain.User;
import org.springframework.stereotype.Component;

@Component("EXTERNAL")
public class ExternalLoanValidator implements LoanValidationStrategy {

    private static final int MAX_BOOKS = 2;
    private static final int MAX_DAYS = 7;

    @Override
    public boolean canBorrow(User user, int currentLoans) {
        return currentLoans < MAX_BOOKS && user.getIsActive();
    }

    @Override
    public int getMaxBooks() {
        return MAX_BOOKS;
    }

    @Override
    public int getMaxDays() {
        return MAX_DAYS;
    }
}
