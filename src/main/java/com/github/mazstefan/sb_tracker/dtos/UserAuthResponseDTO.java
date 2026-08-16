package com.github.mazstefan.sb_tracker.dtos;

public class UserAuthResponseDTO {
    
    private Long token;
    private String type = "Bearer";
    private Long id;
    private String email;

    public UserAuthResponseDTO(Long token, String type, Long id, String email) {
        this.token = token;
        this.type = type;
        this.id = id;
        this. email = email;
    }

    public Long getToken() { return token; }

    public String getType() { return type; }

    public Long getId() { return id; }

    public String getEmail() { return email; }
}
