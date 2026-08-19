package com.github.mazstefan.sb_tracker.services;

import com.github.mazstefan.sb_tracker.dtos.UserLoginDTO;
import com.github.mazstefan.sb_tracker.dtos.UserPasswordUpdateDTO;
import com.github.mazstefan.sb_tracker.dtos.UserRegistrationDTO;
import com.github.mazstefan.sb_tracker.dtos.UserResponseDTO;
import com.github.mazstefan.sb_tracker.dtos.UserAuthResponseDTO;
import com.github.mazstefan.sb_tracker.entities.User;
import com.github.mazstefan.sb_tracker.entities.enums.Role;
import com.github.mazstefan.sb_tracker.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        //TODO: Replace with real JWT token generation in week 2
        String dummyToken = "1234L";

        return mapToAuthResponseDTO(dummyToken, user);
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

    private UserAuthResponseDTO mapToAuthResponseDTO(String token, User user) {
        return new UserAuthResponseDTO(
            token,
            user.getId(),
            user.getEmail()
        );
    }
}
