package org.bibliodigit.domain.strategy;

import org.bibliodigit.domain.User;
import org.springframework.stereotype.Component;

@Component("TEACHER")
public class TeacherLoanValidator implements LoanValidationStrategy {

    private static final int MAX_BOOKS = 5;
    private static final int MAX_DAYS = 30;

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
