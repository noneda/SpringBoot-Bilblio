package org.bibliodigit.api.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;
    
    @NotNull(message = "Year is required")
    @Min(value = 1000, message = "Year must be greater than 999")
    @Max(value = 9999, message = "Year must be less than 10000")
    private Integer year;
    
    @NotNull(message = "Author ID is required")
    private Long authorId;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
