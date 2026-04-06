package com.clothes.backend.service;

import com.clothes.backend.entity.Order;
import com.clothes.backend.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmationEmail(User user, Order order) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(user.getEmail());
        helper.setSubject("Order Confirmation - #" + order.getId());
        helper.setText("<h1>Order Confirmation</h1>" +
                "<p>Hi " + user.getFullName() + ",</p>" +
                "<p>Thank you for your order! Your order #" + order.getId() + " has been received and is being processed.</p>" +
                "<p>Total amount: " + order.getTotalAmount() + " VND</p>" +
                "<p>Shipping address: " + order.getShippingAddress() + "</p>", true);

        mailSender.send(message);
    }

    public void sendLowStockAlert(String productName, String variantSku, int quantity) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo("admin@clothes.com");
        helper.setSubject("Low Stock Alert: " + productName);
        helper.setText("<p>Product: " + productName + "</p>" +
                "<p>Variant SKU: " + variantSku + "</p>" +
                "<p>Current Quantity: " + quantity + "</p>", true);

        mailSender.send(message);
    }
}
