package org.bibliodigit.api.mapper;

import org.bibliodigit.api.dto.res.LoanResponse;
import org.bibliodigit.domain.Stock;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {


    public LoanResponse toResponse(Stock stock) {
        return LoanResponse.builder()
            .id(stock.getId())
            .bookId(stock.getBook() != null ? stock.getBook().getId() : null)
            .bookTitle(stock.getBook() != null ? stock.getBook().getTitle() : null)
            .bookIsbn(stock.getBook() != null ? stock.getBook().getIsbn() : null)
            .authorName(stock.getBook() != null && stock.getBook().getAuthor() != null 
                ? stock.getBook().getAuthor().getName() : null)
            .userId(stock.getUser() != null ? stock.getUser().getId() : null)
            .userName(stock.getUser() != null ? stock.getUser().getName() : null)
            .userEmail(stock.getUser() != null ? stock.getUser().getEmail() : null)
            .userType(stock.getUser() != null && stock.getUser().getTypeUser() != null
                ? stock.getUser().getTypeUser().getType() : null)
            .availability(stock.getAvailability())
            .status(stock.getStatus() != null ? stock.getStatus().name() : null)
            .departureDate(stock.getDepartureDate())
            .deliveryDate(stock.getDeliveryDate())
            .actualReturnDate(stock.getActualReturnDate())
            .daysOverdue(stock.getDaysOverdue())
            .fine(stock.getFine())
            .build();
    }
}
