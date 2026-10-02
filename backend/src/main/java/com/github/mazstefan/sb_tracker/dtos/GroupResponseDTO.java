package com.github.mazstefan.sb_tracker.dtos;

import java.util.List;

public record GroupResponseDTO(
    Long id, 
    String name, 
    String inviteCode, 
    List<String> memberNames
) {}
