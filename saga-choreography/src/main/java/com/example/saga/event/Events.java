package com.example.saga.event;

public class Events {
    public record OrderCreatedEvent(String orderId, String customerId, double amount) {}
    public record PaymentSuccessEvent(String orderId) {}
    public record ShippingSuccessEvent(String orderId) {}
    public record ShippingFailedEvent(String orderId, String reason) {}
    public record CompensatePaymentEvent(String orderId) {}
    public record RefundSuccessEvent(String orderId) {}
}
