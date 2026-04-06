package com.clothes.backend.service;

import com.clothes.backend.dto.request.ReviewRequest;
import com.clothes.backend.entity.Review;
import com.clothes.backend.entity.User;

import java.util.List;

public interface ReviewService {
    Review addReview(User user, ReviewRequest request);
    List<Review> getReviewsByProduct(Long productId);
    void deleteReview(Long reviewId, User user);
    boolean canUserReviewProduct(Long userId, Long productId);
}
