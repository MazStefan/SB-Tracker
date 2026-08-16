package com.github.mazstefan.sb_tracker.dtos;

import java.time.LocalDateTime;

public class UserResponseDTO {
    
    private Long id;
    private String email;
    private LocalDateTime createdAt;

    public UserResponseDTO(Long id, String email, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    
    public String getEmail() { return email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
