package com.github.mazstefan.sb_tracker.dtos;

import jakarta.validation.constraints.NotBlank;

public record GroupJoinDTO(
    @NotBlank(message = "Invite code is required") String inviteCode
) {}
