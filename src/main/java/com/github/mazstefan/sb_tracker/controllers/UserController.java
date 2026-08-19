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
import com.github.mazstefan.sb_tracker.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
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

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtUtil.generateToken(loginDTO.getEmail());

        return ResponseEntity.ok(new UserAuthResponseDTO(token, userDetails.getId(), userDetails.getUsername()));
    }
    
    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(
            Authentication authentication,
            @Valid @RequestBody UserPasswordUpdateDTO passwordUpdateDTO) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Long id = userDetails.getId();

        userService.updatePassword(id, passwordUpdateDTO);

        return ResponseEntity.ok("Password updated successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(Authentication authentication) {
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Long currentUserId = userDetails.getId(); 
        
        UserResponseDTO getResponse = userService.getUserProfile(currentUserId);

        return ResponseEntity.ok(getResponse);
    }
}
