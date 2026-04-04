package com.clothes.backend.controller;

import com.clothes.backend.entity.Product;
import com.clothes.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            
        Pageable paging = PageRequest.of(page, size);
        
        Page<Product> pageProducts;
        if (keyword == null || keyword.isEmpty()) {
            pageProducts = productRepository.findAll(paging);
        } else {
            pageProducts = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, paging);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("products", pageProducts.getContent());
        response.put("currentPage", pageProducts.getNumber());
        response.put("totalItems", pageProducts.getTotalElements());
        response.put("totalPages", pageProducts.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
