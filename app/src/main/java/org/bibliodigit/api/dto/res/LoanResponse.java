package org.bibliodigit.api.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {
    
    private Long id;
    
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private String authorName;
    
    private Long userId;
    private String userName;
    private String userEmail;
    private String userType;
    
    private Boolean availability;
    private String status;
    private LocalDateTime departureDate;
    private LocalDateTime deliveryDate;
    private LocalDateTime actualReturnDate;
    
    private Long daysOverdue;
    private BigDecimal fine;
}
