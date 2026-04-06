package com.clothes.backend.controller;

import com.clothes.backend.entity.User;
import com.clothes.backend.repository.UserRepository;
import com.clothes.backend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PutMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Map<String, Object> data) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (data.containsKey("fullName")) user.setFullName((String) data.get("fullName"));
        if (data.containsKey("email")) user.setEmail((String) data.get("email"));
        if (data.containsKey("phoneNumber")) user.setPhoneNumber((String) data.get("phoneNumber"));
        
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Map<String, String> data) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu cũ không chính xác!");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
