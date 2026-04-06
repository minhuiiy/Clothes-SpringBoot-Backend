package com.clothes.backend.controller;

import com.clothes.backend.entity.Product;
import com.clothes.backend.entity.User;
import com.clothes.backend.repository.UserRepository;
import com.clothes.backend.security.services.UserDetailsImpl;
import com.clothes.backend.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<Product>> getWishlist(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(wishlistService.getWishlistByUser(user));
    }

    @PostMapping("/add/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> addToWishlist(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        wishlistService.addToWishlist(user, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<?> removeFromWishlist(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        wishlistService.removeFromWishlist(user, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check/{productId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Boolean> checkInWishlist(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(wishlistService.isInWishlist(user, productId));
    }
}
