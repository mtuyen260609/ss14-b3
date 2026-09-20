package com.example.saga.controller;

import com.example.saga.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
public class DemoController {
    private final OrderService orderService;

    public DemoController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order/success")
    public String testSuccess() {
        orderService.createOrder(UUID.randomUUID().toString().substring(0, 8), false);
        return "Đã kích hoạt luồng thành công. Xem log console.";
    }

    @GetMapping("/order/fail")
    public String testFail() {
        orderService.createOrder(UUID.randomUUID().toString().substring(0, 8), true);
        return "Đã kích hoạt luồng bù trừ (thất bại). Xem log console.";
    }
}
