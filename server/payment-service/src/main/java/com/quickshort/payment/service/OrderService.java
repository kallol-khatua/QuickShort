package com.quickshort.payment.service;

import com.quickshort.payment.dto.OrderDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDto);

    OrderDto verifyPayment(String paymentId, String orderId, String signature);

    OrderDto verifyRepayPayment(String paymentId, String orderId, String signature);

    List<OrderDto> getAllOrders(UUID workspaceId);

    OrderDto createRepayOrder(OrderDto orderDto);

    OrderDto cancelOrder(UUID orderId);
}
