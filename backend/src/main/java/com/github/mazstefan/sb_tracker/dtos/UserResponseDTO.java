package com.github.mazstefan.sb_tracker.dtos;

import java.time.LocalDateTime;

public class UserResponseDTO {
    
    private Long id;
    private String email;
    private LocalDateTime createdAt;
    private GroupResponseDTO group;

    public UserResponseDTO(Long id, String email, LocalDateTime createdAt, GroupResponseDTO group) {
        this.id = id;
        this.email = email;
        this.createdAt = createdAt;
        this.group = group;
    }

    public Long getId() { return id; }
    
    public String getEmail() { return email; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public GroupResponseDTO getGroup() { return group; }
}
