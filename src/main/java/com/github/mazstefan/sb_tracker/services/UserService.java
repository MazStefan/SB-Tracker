package com.github.mazstefan.sb_tracker.services;

import com.github.mazstefan.sb_tracker.dtos.UserLoginDTO;
import com.github.mazstefan.sb_tracker.dtos.UserPasswordUpdateDTO;
import com.github.mazstefan.sb_tracker.dtos.UserRegistrationDTO;
import com.github.mazstefan.sb_tracker.dtos.UserResponseDTO;
import com.github.mazstefan.sb_tracker.dtos.UserAuthResponseDTO;
import com.github.mazstefan.sb_tracker.entities.User;
import com.github.mazstefan.sb_tracker.entities.enums.Role;
import com.github.mazstefan.sb_tracker.repositories.UserRepository;
import com.github.mazstefan.sb_tracker.security.CustomUserDetails;
import com.github.mazstefan.sb_tracker.security.JwtUtil;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("An account with this email already exists");
        }

        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        return mapToResponseDTO(savedUser);
    }

    public UserAuthResponseDTO loginUser(UserLoginDTO loginDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtUtil.generateToken(userDetails.getUsername());

        return new UserAuthResponseDTO(token, userDetails.getId(), userDetails.getUsername());
    }

    public void updatePassword(Long userId, UserPasswordUpdateDTO passwordUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(passwordUpdateDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("The old password provided is incorrect");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        userRepository.save(user);
    }

    public UserResponseDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return mapToResponseDTO(user);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getEmail(),
            user.getCreatedAt()
        );
    }
}
