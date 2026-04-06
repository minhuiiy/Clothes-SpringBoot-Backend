package com.clothes.backend.repository;

import com.clothes.backend.entity.Order;
import com.clothes.backend.entity.User;
import com.clothes.backend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    
    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status != 'CANCELLED'")
    java.math.BigDecimal getTotalRevenue();

    @org.springframework.data.jpa.repository.Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED'")
    java.math.BigDecimal getRealRevenue();
    
    long countByStatus(OrderStatus status);
    
    List<Order> findTop10ByOrderByCreatedAtDesc();

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i WHERE o.user.id = :userId AND i.variant.product.id = :productId AND o.status = :status")
    boolean hasPurchasedProduct(@org.springframework.data.repository.query.Param("userId") Long userId, 
                               @org.springframework.data.repository.query.Param("productId") Long productId, 
                               @org.springframework.data.repository.query.Param("status") OrderStatus status);
}
