package com.example.saga.service;

import com.example.saga.event.Events.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ShippingService {
    private final ApplicationEventPublisher publisher;

    public ShippingService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Async
    @EventListener
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        System.out.println("[ShippingService] Nhận PaymentSuccessEvent. Đang xử lý giao hàng cho đơn " + event.orderId());
        
        if (event.orderId().contains("-FAIL")) {
            System.out.println("[ShippingService] Lỗi địa chỉ không hợp lệ!");
            publisher.publishEvent(new ShippingFailedEvent(event.orderId(), "Địa chỉ không hợp lệ"));
        } else {
            System.out.println("[ShippingService] Tạo vận đơn thành công!");
            publisher.publishEvent(new ShippingSuccessEvent(event.orderId()));
        }
    }
}
