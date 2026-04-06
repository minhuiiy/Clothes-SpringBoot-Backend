package com.clothes.backend.service;

import com.clothes.backend.entity.Product;
import com.clothes.backend.entity.User;
import com.clothes.backend.entity.Wishlist;
import com.clothes.backend.repository.ProductRepository;
import com.clothes.backend.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductRepository productRepository;

    private static final int MAX_WISHLIST_ITEMS = 50;

    public List<Product> getWishlistByUser(User user) {
        return wishlistRepository.findByUser(user)
                .stream()
                .map(Wishlist::getProduct)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addToWishlist(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

        if (wishlistRepository.existsByUserAndProduct(user, product)) {
            return;
        }

        if (wishlistRepository.countByUser(user) >= MAX_WISHLIST_ITEMS) {
            throw new RuntimeException("Danh sách yêu thích đã đầy (tối đa " + MAX_WISHLIST_ITEMS + " sản phẩm)!");
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();
        wishlistRepository.save(wishlist);
    }

    @Transactional
    public void removeFromWishlist(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));
        wishlistRepository.deleteByUserAndProduct(user, product);
    }

    public boolean isInWishlist(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));
        return wishlistRepository.existsByUserAndProduct(user, product);
    }
}
