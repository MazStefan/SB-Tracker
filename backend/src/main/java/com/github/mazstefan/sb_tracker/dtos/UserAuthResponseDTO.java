package com.github.mazstefan.sb_tracker.dtos;

public record UserAuthResponseDTO(String token, String type, Long id, String email) {
    
    public UserAuthResponseDTO(String token, Long id, String email) {
        this(token, "Bearer", id, email);
    }
}
