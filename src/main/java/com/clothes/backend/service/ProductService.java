package com.clothes.backend.service;

import com.clothes.backend.dto.request.ProductRequest;
import com.clothes.backend.dto.response.ProductResponse;
import com.clothes.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ProductService {
    Page<ProductResponse> getAllProducts(Specification<Product> spec, Pageable pageable);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}
