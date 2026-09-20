package com.example.saga.service;

import com.example.saga.event.Events.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final ApplicationEventPublisher publisher;

    public PaymentService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Async
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        System.out.println("[PaymentService] Nhận OrderCreatedEvent. Trừ tiền tài khoản khách hàng cho đơn " + event.orderId());
        publisher.publishEvent(new PaymentSuccessEvent(event.orderId()));
    }

    @Async
    @EventListener
    public void onCompensatePayment(CompensatePaymentEvent event) {
        System.out.println("[PaymentService] Nhận CompensatePaymentEvent. Hoàn tiền cho đơn " + event.orderId());
        publisher.publishEvent(new RefundSuccessEvent(event.orderId()));
    }
}
