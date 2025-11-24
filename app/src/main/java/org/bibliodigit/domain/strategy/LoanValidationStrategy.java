package org.bibliodigit.domain.strategy;

import org.bibliodigit.domain.User;


public interface LoanValidationStrategy {
    
    boolean canBorrow(User user, int currentLoans);
    
    int getMaxBooks();
    
    int getMaxDays();
}
