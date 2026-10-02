package com.github.mazstefan.sb_tracker.dtos;

import jakarta.validation.constraints.NotBlank;

public record GroupRequestDTO(
    @NotBlank(message = "Group name is required") String name
) {}
