package com.github.mazstefan.sb_tracker.controllers;

import com.github.mazstefan.sb_tracker.dtos.UserLoginDTO;
import com.github.mazstefan.sb_tracker.dtos.UserPasswordUpdateDTO;
import com.github.mazstefan.sb_tracker.dtos.UserRegistrationDTO;
import com.github.mazstefan.sb_tracker.dtos.UserResponseDTO;
import com.github.mazstefan.sb_tracker.dtos.UserAuthResponseDTO;
import com.github.mazstefan.sb_tracker.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestBody UserRegistrationDTO registrationDTO) {

        UserResponseDTO createdUser = userService.registerUser(registrationDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthResponseDTO> loginUser(
            @Valid @RequestBody UserLoginDTO loginDTO) {

        UserAuthResponseDTO authResponse = userService.loginUser(loginDTO);

        return ResponseEntity.ok(authResponse);
    }
    
    @PostMapping("/{id}/password")
    public ResponseEntity<String> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UserPasswordUpdateDTO passwordUpdateDTO) {
        
        userService.updatePassword(id, passwordUpdateDTO);

        return ResponseEntity.ok("Password updated successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile() {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L; 
        
        UserResponseDTO getResponse = userService.getUserProfile(currentUserId);

        return ResponseEntity.ok(getResponse);
    }
}
