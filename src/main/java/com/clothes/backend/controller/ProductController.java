package com.clothes.backend.controller;

import com.clothes.backend.entity.Product;
import com.clothes.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.jpa.domain.Specification;
import com.clothes.backend.service.ProductSpecification;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Boolean isPromoted,
            @RequestParam(required = false) Boolean isFeatured) {
            
        Sort sortOrder = getSortOrder(sort);
        Pageable paging = PageRequest.of(page, size, sortOrder);
        
        Specification<Product> spec = Specification.where(ProductSpecification.hasName(keyword))
                .and(ProductSpecification.hasCategory(categoryId))
                .and(ProductSpecification.hasBrand(brandId))
                .and(ProductSpecification.hasColor(color))
                .and(ProductSpecification.isPromoted(isPromoted))
                .and(ProductSpecification.isFeatured(isFeatured))
                .and(ProductSpecification.hasPriceBetween(minPrice, maxPrice));
        
        Page<Product> pageProducts = productRepository.findAll(spec, paging);

        Map<String, Object> response = new HashMap<>();
        response.put("products", pageProducts.getContent());
        response.put("currentPage", pageProducts.getNumber());
        response.put("totalItems", pageProducts.getTotalElements());
        response.put("totalPages", pageProducts.getTotalPages());

        return ResponseEntity.ok(response);
    }

    private Sort getSortOrder(String sort) {
        switch (sort) {
            case "price_asc":
                return Sort.by("price").ascending();
            case "price_desc":
                return Sort.by("price").descending();
            case "name_asc":
                return Sort.by("name").ascending();
            case "name_desc":
                return Sort.by("name").descending();
            case "oldest":
                return Sort.by("createdAt").ascending();
            case "featured":
                return Sort.by("isFeatured").descending().and(Sort.by("createdAt").descending());
            case "best_selling":
                return Sort.by("soldCount").descending();
            case "newest":
            default:
                return Sort.by("createdAt").descending();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
