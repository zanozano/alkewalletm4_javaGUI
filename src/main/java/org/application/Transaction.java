package org.application;

public record Transaction(
        double amount,
        String transactionDate,
        String senderId,
        String receiverId,
        String currencyCode,
        String type) {
}
