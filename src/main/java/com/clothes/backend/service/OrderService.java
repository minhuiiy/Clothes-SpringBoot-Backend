package com.clothes.backend.service;

import com.clothes.backend.entity.*;
import com.clothes.backend.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Order createOrder(Long userId, String shippingAddress, String phoneNumber, String note, String paymentMethod) {
        log.info("Starting order creation for user id: {}", userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Cart not found for user id: {}", userId);
                    return new RuntimeException("Giỏ hàng trống!");
                });

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng không có sản phẩm nào!");
        }

        BigDecimal subtotal = cart.getItems().stream()
                .map(item -> (item.getVariant().getProduct().getPrice().add(item.getVariant().getAdditionalPrice()))
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFee = calculateShipping(shippingAddress, subtotal);
        BigDecimal tax = calculateTax(subtotal);

        BigDecimal totalAmount = subtotal.add(shippingFee).add(tax);

        String paymentStatus = "BANK_TRANSFER".equals(paymentMethod) ? "PENDING" : "PENDING";

        Order order = Order.builder()
                .user(cart.getUser())
                .status(OrderStatus.PENDING)
                .shippingAddress(shippingAddress)
                .phoneNumber(phoneNumber)
                .note(note)
                .totalAmount(totalAmount)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .items(new ArrayList<>())
                .build();

        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getVariant();
            Inventory inventory = inventoryRepository.findByVariantId(variant.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin kho cho sản phẩm: " + variant.getSku()));

            if (inventory.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + variant.getSku() + " đã hết hàng!");
            }

            // Deduct stock
            inventory.setQuantity(inventory.getQuantity() - cartItem.getQuantity());
            inventoryRepository.save(inventory);

            // Update product sold count
            Product product = variant.getProduct();
            product.setSoldCount(product.getSoldCount() + cartItem.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .variant(variant)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice().add(variant.getAdditionalPrice()))
                    .build();
            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("Order created successfully with id: {}", savedOrder.getId());
        
        try {
            emailService.sendOrderConfirmationEmail(cart.getUser(), savedOrder);
        } catch (Exception e) {
            log.error("Failed to send confirmation email for order id: {}", savedOrder.getId(), e);
        }

        return savedOrder;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID phải khác null");
        }
        Long id = orderId;
        log.info("Updating status for order id: {} to {}", id, status);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại!"));
        
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updatePaymentStatus(Long orderId, String paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại!"));
        order.setPaymentStatus(paymentStatus);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public Order cancelOrder(Long orderId, Long userId) {
        log.info("User {} cancelling order id: {}", userId, orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại!"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này!");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng khi đang ở trạng thái Chờ xác nhận (PENDING)!");
        }

        // Restock inventory
        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getVariant();
            Inventory inventory = inventoryRepository.findByVariantId(variant.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy kho để hoàn lại sản phẩm!"));
            
            inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);

            // Update sold count
            Product product = variant.getProduct();
            product.setSoldCount(Math.max(0, product.getSoldCount() - item.getQuantity()));
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại!"));
    }

    private BigDecimal calculateShipping(String address, BigDecimal amount) {
        if (amount.compareTo(new BigDecimal(500000)) >= 0) { // Free shipping for orders > 500k
            return BigDecimal.ZERO;
        }
        return new BigDecimal(30000); // Flat rate 30k
    }

    private BigDecimal calculateTax(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.1")); // 10% VAT
    }
}
