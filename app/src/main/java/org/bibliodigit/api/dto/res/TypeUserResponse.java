package org.bibliodigit.api.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeUserResponse {
    
    private Long id;
    private String type;
    private String description;
    private Integer userCount;
}
