package com.clothes.backend.controller;

import com.clothes.backend.entity.Order;
import com.clothes.backend.entity.User;
import com.clothes.backend.repository.UserRepository;
import com.clothes.backend.security.services.UserDetailsImpl;
import com.clothes.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class UserOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Order> createOrder(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody Map<String, Object> orderData) {
        
        String address = (String) orderData.get("shippingAddress");
        if (address == null) {
            address = (String) orderData.get("address");
        }
        
        String phoneNumber = (String) orderData.get("phoneNumber");
        String note = (String) orderData.get("note");
        String paymentMethod = (String) orderData.get("paymentMethod");
        if (paymentMethod == null) paymentMethod = "COD";
        
        Order order = orderService.createOrder(userDetails.getId(), address, phoneNumber, note, paymentMethod);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<Order>> getUserOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(orderService.getOrdersByUser(user));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Order> getOrderById(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (!order.getUser().getId().equals(userDetails.getId()) && !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_STAFF"))) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Order> cancelOrder(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        Order cancelledOrder = orderService.cancelOrder(id, userDetails.getId());
        return ResponseEntity.ok(cancelledOrder);
    }
}
