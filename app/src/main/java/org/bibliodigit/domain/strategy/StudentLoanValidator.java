package org.bibliodigit.domain.strategy;

import org.bibliodigit.domain.User;
import org.springframework.stereotype.Component;

@Component("STUDENT")
public class StudentLoanValidator implements LoanValidationStrategy {

    private static final int MAX_BOOKS = 3;
    private static final int MAX_DAYS = 14;

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
