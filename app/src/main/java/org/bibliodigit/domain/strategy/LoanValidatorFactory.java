package org.bibliodigit.domain.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoanValidatorFactory {

    private final Map<String, LoanValidationStrategy> validators;

    public LoanValidationStrategy getValidator(String userType) {
        LoanValidationStrategy validator = validators.get(userType);
        
        if (validator == null) {
            throw new RuntimeException("No validator found for user type: " + userType);
        }
        
        return validator;
    }
}
