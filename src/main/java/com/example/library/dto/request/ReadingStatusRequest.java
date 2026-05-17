package com.example.library.dto.request;

import com.example.library.entity.ReadingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingStatusRequest {
    
    @NotNull(message = "Status is required")
    private ReadingStatus.Status status;
}
