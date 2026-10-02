package com.github.mazstefan.sb_tracker.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.mazstefan.sb_tracker.dtos.GroupJoinDTO;
import com.github.mazstefan.sb_tracker.dtos.GroupRequestDTO;
import com.github.mazstefan.sb_tracker.dtos.GroupResponseDTO;
import com.github.mazstefan.sb_tracker.security.CustomUserDetails;
import com.github.mazstefan.sb_tracker.services.GroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    
    @PostMapping
    public ResponseEntity<GroupResponseDTO> createGroup(
            Authentication authentication,
            @RequestBody @Valid GroupRequestDTO request) {

        Long currentUserId = extractUserId(authentication);
        
        return ResponseEntity.ok(groupService.createGroup(request, currentUserId));
    }

    @PostMapping("/join")
    public ResponseEntity<GroupResponseDTO> joinGroup(
            Authentication authentication,
            @RequestBody @Valid GroupJoinDTO request) {

        Long currentUserId = extractUserId(authentication);

        return ResponseEntity.ok(groupService.joinGroup(request.inviteCode(), currentUserId));
    }

    @PostMapping("/leave")
    public ResponseEntity<Void> leaveGroup(Authentication authentication) {

        Long currentUserId = extractUserId(authentication);
        
        groupService.leaveGroup(currentUserId);
        return ResponseEntity.ok().build();
    }
    
    private Long extractUserId(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    return userDetails.getId();
    }
}
