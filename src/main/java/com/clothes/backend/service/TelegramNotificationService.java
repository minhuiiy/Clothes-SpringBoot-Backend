package com.clothes.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TelegramNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationService.class);
    private final RestTemplate restTemplate;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.chat-id}")
    private String chatId;

    @Value("${telegram.bot.enabled:true}")
    private boolean enabled;

    public TelegramNotificationService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendMessage(String message) {
        if (!enabled) {
            logger.info("Telegram notification is disabled. Message: {}", message);
            return;
        }

        if (botToken == null || botToken.isEmpty() || botToken.equals("YOUR_BOT_TOKEN_HERE") ||
                chatId == null || chatId.isEmpty() || chatId.equals("YOUR_CHAT_ID_HERE")) {
            logger.warn("Telegram bot token or chat ID is not configured properly.");
            return;
        }

        try {
            String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);
            var request = java.util.Map.of("chat_id", chatId, "text", message);
            restTemplate.postForObject(url, request, String.class);
            logger.info("Sent Telegram notification successfully.");
        } catch (Exception e) {
            logger.error("Failed to send Telegram notification: {}", e.getMessage());
        }
    }

}
