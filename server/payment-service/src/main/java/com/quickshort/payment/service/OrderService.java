package com.quickshort.payment.service;

import com.quickshort.payment.dto.OrderDto;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDto);

    OrderDto verifyPayment(String paymentId, String orderId, String signature);
}
