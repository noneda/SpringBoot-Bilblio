package org.bibliodigit.api.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeUserRequest {
    
    @NotBlank(message = "Type is required")
    @Size(min = 2, max = 50, message = "Type must be between 2 and 50 characters")
    private String type; 
    
    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
}
