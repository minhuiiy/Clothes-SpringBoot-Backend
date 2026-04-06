package com.clothes.backend.repository;

import com.clothes.backend.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductId(Long productId);
    java.util.Optional<ProductVariant> findByProductIdAndSizeAndColor(Long productId, String size, String color);
    ProductVariant findBySku(String sku);
}
