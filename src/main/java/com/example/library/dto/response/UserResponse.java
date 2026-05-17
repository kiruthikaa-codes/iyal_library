package com.example.library.dto.response;

import com.example.library.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String name;
    private String email;
    private User.Role role;
    private Boolean isProfilePublic;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
