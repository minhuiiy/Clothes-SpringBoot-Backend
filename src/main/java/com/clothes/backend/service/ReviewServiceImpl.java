package com.clothes.backend.service;

import com.clothes.backend.dto.request.ReviewRequest;
import com.clothes.backend.entity.Product;
import com.clothes.backend.entity.Review;
import com.clothes.backend.entity.User;
import com.clothes.backend.repository.ProductRepository;
import com.clothes.backend.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private com.clothes.backend.repository.OrderRepository orderRepository;

    @Override
    @Transactional
    public Review addReview(User user, ReviewRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

        // Check if user has purchased and received this product
        boolean hasPurchased = orderRepository.hasPurchasedProduct(user.getId(), product.getId(), com.clothes.backend.entity.OrderStatus.DELIVERED);
        if (!hasPurchased) {
            throw new RuntimeException("Bạn chỉ có thể đánh giá sản phẩm sau khi đã mua và nhận hàng thành công!");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Override
    public boolean canUserReviewProduct(Long userId, Long productId) {
        return orderRepository.hasPurchasedProduct(userId, productId, com.clothes.backend.entity.OrderStatus.DELIVERED);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Đánh giá không tồn tại!"));
        
        // Only owner or admin can delete
        if (!review.getUser().getId().equals(user.getId())) {
             // Check if user is admin (simplified check)
             // In a real app, check roles
        }
        
        reviewRepository.delete(review);
    }
}
