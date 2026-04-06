package com.clothes.backend.service;

import com.clothes.backend.entity.Product;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> hasName(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isEmpty()) return null;
            String search = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), search),
                cb.like(cb.lower(root.get("description")), search)
            );
        };
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) -> categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> hasCategorySlug(String slug) {
        return (root, query, cb) -> slug == null || slug.isEmpty() ? null : cb.equal(root.join("category").get("slug"), slug);
    }

    public static Specification<Product> hasBrand(Long brandId) {
        return (root, query, cb) -> brandId == null ? null : cb.equal(root.get("brand").get("id"), brandId);
    }

    public static Specification<Product> hasColor(String color) {
        return (root, query, cb) -> {
            if (color == null || color.isEmpty()) return null;
            return cb.equal(root.join("variants").get("color"), color);
        };
    }

    public static Specification<Product> hasSize(String size) {
        return (root, query, cb) -> {
            if (size == null || size.isEmpty()) return null;
            return cb.equal(root.join("variants").get("size"), size);
        };
    }

    public static Specification<Product> isFeatured(Boolean featured) {
        return (root, query, cb) -> (featured == null || !featured) ? null : cb.isTrue(root.get("isFeatured"));
    }

    public static Specification<Product> hasPriceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) return cb.between(root.get("price"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.lessThanOrEqualTo(root.get("price"), max);
        };
    }
}
