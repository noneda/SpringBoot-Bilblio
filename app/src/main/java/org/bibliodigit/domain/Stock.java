package org.bibliodigit.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private Boolean availability = true;  

    @Column(name = "departure_date")
    private LocalDateTime departureDate;  

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;  

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private LoanStatus status = LoanStatus.AVAILABLE;

    @Column(name = "actual_return_date")
    private LocalDateTime actualReturnDate;  

    @Column(precision = 10, scale = 2)
    private BigDecimal fine;  

    public long getDaysOverdue() {
        if (status == LoanStatus.RETURNED || deliveryDate == null) {
            return 0;
        }
        
        LocalDateTime compareDate = actualReturnDate != null ? actualReturnDate : LocalDateTime.now();
        
        if (compareDate.isAfter(deliveryDate)) {
            return ChronoUnit.DAYS.between(deliveryDate, compareDate);
        }
        
        return 0;
    }

    public BigDecimal calculateFine() {
        long daysOverdue = getDaysOverdue();
        if (daysOverdue > 0) {
            return BigDecimal.valueOf(daysOverdue);
        }
        return BigDecimal.ZERO;
    }


    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE && 
               LocalDateTime.now().isAfter(deliveryDate);
    }
}
