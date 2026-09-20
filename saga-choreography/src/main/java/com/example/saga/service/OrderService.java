package com.example.saga.service;

import com.example.saga.event.Events.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final ApplicationEventPublisher publisher;

    public OrderService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void createOrder(String orderId, boolean simulateShippingFail) {
        System.out.println("[OrderService] Tạo đơn hàng: " + orderId + " (Trạng thái: PENDING)");
        // Truyền thông tin giả lập lỗi giao hàng vào ID để ShippingService nhận biết
        String finalOrderId = simulateShippingFail ? orderId + "-FAIL" : orderId;
        publisher.publishEvent(new OrderCreatedEvent(finalOrderId, "CUST01", 100.0));
    }

    @Async
    @EventListener
    public void onShippingSuccess(ShippingSuccessEvent event) {
        System.out.println("[OrderService] Nhận ShippingSuccessEvent. Đơn hàng " + event.orderId() + " hoàn thành. (Trạng thái: COMPLETED)");
    }

    @Async
    @EventListener
    public void onShippingFailed(ShippingFailedEvent event) {
        System.out.println("[OrderService] Nhận ShippingFailedEvent cho đơn " + event.orderId() + ". Bắt đầu bù trừ...");
        publisher.publishEvent(new CompensatePaymentEvent(event.orderId()));
    }

    @Async
    @EventListener
    public void onRefundSuccess(RefundSuccessEvent event) {
        System.out.println("[OrderService] Nhận RefundSuccessEvent. Đơn hàng " + event.orderId() + " đã bị hủy. (Trạng thái: CANCELLED)");
    }
}
