package com.clothes.backend.service;

import com.clothes.backend.dto.request.ProductRequest;
import com.clothes.backend.dto.request.VariantRequest;
import com.clothes.backend.dto.response.ProductResponse;
import com.clothes.backend.dto.response.VariantResponse;
import com.clothes.backend.entity.*;
import com.clothes.backend.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductVariantRepository variantRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    @Cacheable(value = "products", key = "#spec.toString() + #pageable.pageNumber")
    public Page<ProductResponse> getAllProducts(Specification<Product> spec, Pageable pageable) {
        log.info("Fetching all products with spec and pageable: {}, {}", spec, pageable);
        return productRepository.findAll(spec, pageable).map(this::convertToResponse);
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public ProductResponse getProductById(Long id) {
        @lombok.NonNull Long productId = java.util.Objects.requireNonNull(id, "Product ID must not be null");
        log.info("Fetching product by id: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", productId);
                    return new RuntimeException("Sản phẩm không tồn tại!");
                });
        return convertToResponse(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {
        try {
            log.info("Creating new product: {}", request.getName());
            @lombok.NonNull Long catId = java.util.Objects.requireNonNull(request.getCategoryId(), "Category ID must not be null");
            Category category = categoryRepository.findById(catId)
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại!"));
            
            Brand brand = null;
            if (request.getBrandId() != null) {
                Long brandId = request.getBrandId();
                brand = brandRepository.findById(brandId)
                        .orElse(null);
            }

            Product product = Product.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .category(category)
                    .brand(brand)
                    .imageUrl(request.getImageUrl())
                    .isFeatured(request.isFeatured())
                    .build();

            Product savedProduct = productRepository.save(product);

            if (request.getVariants() != null && !request.getVariants().isEmpty()) {
                for (VariantRequest vReq : request.getVariants()) {
                    updateOrCreateVariant(savedProduct, vReq);
                }
            } else if (request.getStock() != null) {
                // Tạo một variant mặc định nếu không có danh sách variants nhưng có số lượng tồn kho
                VariantRequest defaultVReq = new VariantRequest();
                defaultVReq.setSize("F"); // Free size
                defaultVReq.setColor(request.getColor() != null ? request.getColor() : "Mặc định");
                defaultVReq.setStock(request.getStock());
                defaultVReq.setAdditionalPrice(java.math.BigDecimal.ZERO);
                defaultVReq.setSku("SKU-" + savedProduct.getId() + "-DEF");
                
                updateOrCreateVariant(savedProduct, defaultVReq);
            }

            Long savedId = savedProduct.getId();
            log.info("Product created successfully with id: {}", savedId != null ? savedId : "unknown");
            return convertToResponse(savedProduct);
        } catch (Exception e) {
            log.error("CRITICAL ERROR during product creation: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        @lombok.NonNull Long productId = java.util.Objects.requireNonNull(id, "Product ID must not be null");
        log.info("Updating product with id: {}", productId.longValue());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

        Long categoryId = java.util.Objects.requireNonNull(request.getCategoryId(), "Category ID must not be null");
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại!"));
        
        Brand brand = null;
        if (request.getBrandId() != null) {
            brand = brandRepository.findById(request.getBrandId())
                    .orElse(null);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setBrand(brand);
        product.setImageUrl(request.getImageUrl());
        product.setFeatured(request.isFeatured());

        Product savedProduct = productRepository.save(product);

        // Update variants and stock
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (VariantRequest vReq : request.getVariants()) {
                updateOrCreateVariant(savedProduct, vReq);
            }
        } else if (request.getStock() != null) {
            // Cập nhật hoặc tạo variant mặc định
            VariantRequest defaultVReq = new VariantRequest();
            defaultVReq.setSize("F");
            defaultVReq.setColor(request.getColor() != null ? request.getColor() : "Mặc định");
            defaultVReq.setStock(request.getStock());
            defaultVReq.setAdditionalPrice(java.math.BigDecimal.ZERO);
            defaultVReq.setSku("SKU-" + savedProduct.getId() + "-DEF");
            
            updateOrCreateVariant(savedProduct, defaultVReq);
        }

        log.info("Product updated successfully: {}", id);
        return convertToResponse(savedProduct);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public void deleteProduct(Long id) {
        java.util.Objects.requireNonNull(id, "Product ID must not be null");
        log.info("Deleting product with id: {}", id);
        productRepository.deleteById(id);
    }

    private ProductResponse convertToResponse(Product product) {
        List<VariantResponse> variants = product.getVariants().stream()
                .map(v -> {
                    @lombok.NonNull Long variantId = java.util.Objects.requireNonNull(v.getId(), "Variant ID must not be null");
                    Inventory inv = inventoryRepository.findByVariantId(variantId).orElse(null);
                    return VariantResponse.builder()
                            .id(variantId)
                            .size(v.getSize())
                            .color(v.getColor())
                            .additionalPrice(v.getAdditionalPrice())
                            .sku(v.getSku())
                            .stock(inv != null ? inv.getQuantity() : 0)
                            .build();
                })
                .collect(Collectors.toList());

        int totalStock = variants.stream().mapToInt(VariantResponse::getStock).sum();

        double avgRating = product.getReviews().stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        @lombok.NonNull Long productId = java.util.Objects.requireNonNull(product.getId(), "Product ID must not be null");
        return ProductResponse.builder()
                .id(productId)
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .imageUrl(product.getImageUrl())
                .isFeatured(product.isFeatured())
                .variants(variants)
                .totalStock(totalStock)
                .averageRating(avgRating)
                .reviewCount(product.getReviews().size())
                .build();
    }

    private void updateOrCreateVariant(@lombok.NonNull Product product, VariantRequest vReq) {
        @lombok.NonNull Long productId = java.util.Objects.requireNonNull(product.getId(), "Product ID must not be null");
        Optional<ProductVariant> existingVariant = variantRepository.findByProductIdAndSizeAndColor(
                productId, vReq.getSize(), vReq.getColor());
        
        ProductVariant variant;
        if (existingVariant.isPresent()) {
            variant = existingVariant.get();
            variant.setAdditionalPrice(vReq.getAdditionalPrice() != null ? vReq.getAdditionalPrice() : variant.getAdditionalPrice());
            variant.setSku(vReq.getSku() != null ? vReq.getSku() : variant.getSku());
        } else {
            variant = ProductVariant.builder()
                    .product(product)
                    .size(vReq.getSize())
                    .color(vReq.getColor())
                    .additionalPrice(vReq.getAdditionalPrice() != null ? vReq.getAdditionalPrice() : BigDecimal.ZERO)
                    .sku(vReq.getSku())
                    .build();
        }
        
        ProductVariant savedVariant = variantRepository.save(variant);
        @lombok.NonNull Long variantId = java.util.Objects.requireNonNull(savedVariant.getId(), "Variant ID must not be null");

        Inventory inventory = inventoryRepository.findByVariantId(variantId)
                .orElse(Inventory.builder().variant(savedVariant).build());
        
        inventory.setQuantity(vReq.getStock() != null ? vReq.getStock() : (inventory.getQuantity() != null ? inventory.getQuantity() : 0));
        inventory.setLowStockThreshold(vReq.getLowStockThreshold() != null ? vReq.getLowStockThreshold() : 10);
        inventoryRepository.save(inventory);
    }
}
