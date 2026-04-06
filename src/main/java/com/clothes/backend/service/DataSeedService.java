package com.clothes.backend.service;

import com.clothes.backend.entity.*;
import com.clothes.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSeedService {

    private final JdbcTemplate jdbcTemplate;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public void reseedDatabase() {
        log.info(">>> STARTING DATABASE RESEED <<<");
        try {
            // 1. Clear all data properly
            clearDatabase();

            // 2. Seed Basic Data
            seedUsers();
            List<Category> categories = seedCategories();
            List<Brand> brands = seedBrands();

            // 3. Seed Products
            seedProducts(categories, brands);
            
            log.info(">>> DATABASE RESEEDED SUCCESSFULLY <<<");
        } catch (Exception e) {
            log.error("CRITICAL ERROR during reseed: {}", e.getMessage(), e);
            throw new RuntimeException("Reseed failed: " + e.getMessage());
        }
    }

    private void clearDatabase() {
        log.info("Deleting existing data from all tables...");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
        
        String[] tables = {
            "order_items", "orders", "reviews", "cart_items", "carts", 
            "inventory", "product_variants", "products", "brands", "categories"
        };
        
        for (String table : tables) {
            try {
                jdbcTemplate.execute("TRUNCATE TABLE " + table);
            } catch (Exception e) {
                log.debug("Table {} could not be truncated: {}", table, e.getMessage());
            }
        }

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
    }

    private void seedUsers() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("$2a$10$Xptf/e.S.RGP4S/m.R.v.eD6vK6G6/xP.Tz.F6J4G6uY9Z7pPzN2S"); // password: admin
            admin.setEmail("admin@clothes.com");
            admin.setFullName("Quản trị viên");
            admin.setRole(Role.ADMIN);
            admin.setProvider("LOCAL");
            userRepository.save(admin);
            log.info("Admin user created.");
        }
    }

    private List<Category> seedCategories() {
        List<Category> cats = new ArrayList<>();
        
        Category cat1 = createCategory("Nam", "nam", "Thời trang Nam cao cấp");
        cats.add(categoryRepository.save(cat1));
        
        Category cat2 = createCategory("Nữ", "nu", "Thời trang Nữ thanh lịch");
        cats.add(categoryRepository.save(cat2));
        
        Category cat3 = createCategory("Phụ kiện", "phu-kien", "Phụ kiện thời trang hiện đại");
        cats.add(categoryRepository.save(cat3));

        log.info("Categories created.");
        return cats;
    }

    private Category createCategory(String name, String slug, String desc) {
        Category c = new Category();
        c.setName(name);
        c.setSlug(slug);
        c.setDescription(desc);
        return c;
    }

    private List<Brand> seedBrands() {
        List<Brand> brands = new ArrayList<>();
        
        Brand b1 = createBrand("Nike", "nike", "Just Do It");
        brands.add(brandRepository.saveAndFlush(b1));
        
        Brand b2 = createBrand("Adidas", "adidas", "Impossible is Nothing");
        brands.add(brandRepository.saveAndFlush(b2));
        
        Brand b3 = createBrand("Uniqlo", "uniqlo", "Lifewear");
        brands.add(brandRepository.saveAndFlush(b3));
        
        Brand b4 = createBrand("Gucci", "gucci", "Luxury Fashion");
        brands.add(brandRepository.saveAndFlush(b4));
        
        Brand b5 = createBrand("Zara", "zara", "Fast Fashion");
        brands.add(brandRepository.saveAndFlush(b5));

        log.info("Brands created and flushed.");
        return brands;
    }

    private Brand createBrand(String name, String slug, String desc) {
        Brand b = new Brand();
        b.setName(name);
        b.setSlug(slug);
        b.setDescription(desc);
        return b;
    }

    private void seedProducts(List<Category> categories, List<Brand> brands) {
        String[] qSizes = {"S", "M", "L", "XL"};
        String[] gSizes = {"39", "40", "41", "42"};
        String[] colors = {"Trắng", "Đen", "Xanh Navy"};

        log.info("Seeding products...");
        createProduct("Áo Thun Nike Sportswear Essential", 
                "Áo thun cotton cao cấp, thoáng mát, phong cách thể thao năng động.",
                new BigDecimal("550000"), categories.get(0), brands.get(0), 
                "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab", qSizes, colors);

        createProduct("Quần Jogger Adidas 3-Stripes", 
                "Thiết kế 3 sọc đặc trưng, chất liệu nỉ mềm mại cho cảm giác thoải mái.",
                new BigDecimal("1200000"), categories.get(0), brands.get(1), 
                "https://images.unsplash.com/photo-1552346154-21d32810abb1", qSizes, colors);

        createProduct("Áo Sơ Mi Oxford Dài Tay Uniqlo", 
                "Chất liệu vải Oxford dày dặn nhưng vẫn mềm mại, phong cách tối giản.",
                new BigDecimal("499000"), categories.get(0), brands.get(2), 
                "https://images.unsplash.com/photo-1596755094514-f87e34085b2c", qSizes, colors);

        createProduct("Váy Hoa Nhí Vintage Zara", 
                "Họa tiết hoa tinh tế, chất liệu voan nhẹ nhàng, bay bổng.",
                new BigDecimal("990000"), categories.get(1), brands.get(4), 
                "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1", qSizes, colors);

        createProduct("Túi Xách Gucci Marmont Small", 
                "Biểu tượng của sự sang trọng với logo GG nổi bật và da chần bông.",
                new BigDecimal("55000000"), categories.get(2), brands.get(3), 
                "https://images.unsplash.com/photo-1584917033904-47e034731c3e", new String[]{"Free size"}, new String[]{"Đen", "Be"});

        createProduct("Giày Nike Air Force 1 '07", 
                "Mẫu giày huyền thoại với phối màu trắng tinh khôi, bền bỉ theo thời gian.",
                new BigDecimal("2900000"), categories.get(2), brands.get(0), 
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff", gSizes, new String[]{"Trắng"});

        log.info("All heavy Products with total links seeded.");
    }

    private void createProduct(String name, String desc, BigDecimal price, Category cat, Brand brand, String img, String[] sizes, String[] colors) {
        log.info("Creating product: {}", name);
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(price);
        p.setCategory(cat);
        p.setBrand(brand);
        p.setImageUrl(img);
        p.setFeatured(true);
        Product savedProduct = productRepository.saveAndFlush(p);

        for (String size : sizes) {
            for (String color : colors) {
                ProductVariant v = new ProductVariant();
                v.setProduct(savedProduct);
                v.setSize(size);
                v.setColor(color);
                v.setSku("TBX-" + savedProduct.getId() + "-" + size.toUpperCase() + "-" + color.toUpperCase().substring(0,1));
                v.setAdditionalPrice(BigDecimal.ZERO);
                ProductVariant savedVariant = variantRepository.saveAndFlush(v);

                Inventory inv = new Inventory();
                inv.setVariant(savedVariant);
                inv.setQuantity(100);
                inv.setLowStockThreshold(10);
                inventoryRepository.saveAndFlush(inv);
            }
        }
    }
}
