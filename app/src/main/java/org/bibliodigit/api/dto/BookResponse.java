package org.bibliodigit.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    
    private Long id;
    private String title;
    private Integer year;
    
    private Long authorId;
    private String authorName;
    private String authorNationality;
    
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
}
