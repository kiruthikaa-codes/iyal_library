package com.example.library.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileVisibilityRequest {
    
    @NotNull(message = "Profile visibility is required")
    private Boolean isPublic;
}
