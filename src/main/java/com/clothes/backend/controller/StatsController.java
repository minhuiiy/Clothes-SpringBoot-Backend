package com.clothes.backend.controller;

import com.clothes.backend.entity.OrderStatus;
import com.clothes.backend.repository.OrderRepository;
import com.clothes.backend.repository.ProductRepository;
import com.clothes.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class StatsController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/summary")
    @Cacheable(value = "admin_stats", key = "'summary'")
    public ResponseEntity<Map<String, Object>> getSummary() {
        log.info("Generating admin statistics summary");
        BigDecimal totalRevenue = orderRepository.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        BigDecimal realRevenue = orderRepository.getRealRevenue();
        if (realRevenue == null) realRevenue = BigDecimal.ZERO;
        
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long totalProducts = productRepository.count();
        long totalUsers = userRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRevenue", totalRevenue);
        stats.put("realRevenue", realRevenue);
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("totalProducts", totalProducts);
        stats.put("totalUsers", totalUsers);
        stats.put("recentOrders", orderRepository.findTop10ByOrderByCreatedAtDesc());

        return ResponseEntity.ok(stats);
    }
}
