package com.example.library.dto.response;

import com.example.library.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private User.Role role;
    
    public AuthResponse(String token, Long id, String name, String email, User.Role role) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
