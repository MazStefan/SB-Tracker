package com.github.mazstefan.sb_tracker.controllers;

import com.github.mazstefan.sb_tracker.dtos.UserLoginDTO;
import com.github.mazstefan.sb_tracker.dtos.UserPasswordUpdateDTO;
import com.github.mazstefan.sb_tracker.dtos.UserRegistrationDTO;
import com.github.mazstefan.sb_tracker.dtos.UserResponseDTO;
import com.github.mazstefan.sb_tracker.dtos.UserAuthResponseDTO;
import com.github.mazstefan.sb_tracker.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.github.mazstefan.sb_tracker.security.CustomUserDetails;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;

    public UserController( UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody UserRegistrationDTO registrationDTO) {

        UserResponseDTO createdUser = userService.registerUser(registrationDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthResponseDTO> loginUser(
            @Valid @RequestBody UserLoginDTO loginDTO) {

                UserAuthResponseDTO responseBody = userService.loginUser(loginDTO);

        return ResponseEntity.ok(responseBody);
    }
    
    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(
            Authentication authentication,
            @Valid @RequestBody UserPasswordUpdateDTO passwordUpdateDTO) {
        
        Long currentUserId = extractUserId(authentication); 

        userService.updatePassword(currentUserId, passwordUpdateDTO);

        return ResponseEntity.ok("Password updated successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(Authentication authentication) {
        Long currentUserId = extractUserId(authentication); 
        
        UserResponseDTO getResponse = userService.getUserProfile(currentUserId);

        return ResponseEntity.ok(getResponse);
    }

    private Long extractUserId(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    return userDetails.getId();
    }
}
