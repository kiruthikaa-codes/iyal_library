package com.example.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    
    private List<ReadingStatusResponse> continueReading;
    private List<ReadingStatusResponse> currentlyReadingByFamily;
    private List<BookResponse> recentlyAdded;
    private List<BookResponse> popularBooks;
}
