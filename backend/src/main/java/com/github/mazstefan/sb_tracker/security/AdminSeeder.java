package com.github.mazstefan.sb_tracker.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.github.mazstefan.sb_tracker.repositories.UserRepository;
import com.github.mazstefan.sb_tracker.entities.enums.Role;
import com.github.mazstefan.sb_tracker.entities.User;

@Component
public class AdminSeeder implements CommandLineRunner{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override 
    public void run(String... args) {
        if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@admin.com");
            
            admin.setPassword(passwordEncoder.encode("adminpass")); 
            
            
            admin.setRole(Role.ADMIN); 
            
            userRepository.save(admin);
            System.out.println("Admin account seeded successfully.");
        }
    }
}
