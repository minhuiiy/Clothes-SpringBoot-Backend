package com.clothes.backend.controller;

import com.clothes.backend.dto.request.ReviewRequest;
import com.clothes.backend.entity.Review;
import com.clothes.backend.entity.User;
import com.clothes.backend.repository.UserRepository;
import com.clothes.backend.security.services.UserDetailsImpl;
import com.clothes.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Review> postReview(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ReviewRequest request) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(reviewService.addReview(user, request));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    @GetMapping("/can-review/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> canReview(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long productId) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(reviewService.canUserReviewProduct(user.getId(), productId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteReview(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        reviewService.deleteReview(id, user);
        return ResponseEntity.ok().build();
    }
}
