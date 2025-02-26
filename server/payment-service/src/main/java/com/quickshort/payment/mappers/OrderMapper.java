package com.quickshort.payment.mappers;

import com.quickshort.payment.dto.OrderDto;
import com.quickshort.payment.models.Order;

public class OrderMapper {
    public static OrderDto maptoOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();

        orderDto.setId(order.getId());
        orderDto.setPlanId(order.getPlanId().getId());
        orderDto.setWorkspaceId(order.getWorkspaceId().getId());
        orderDto.setAmount(order.getAmount());
        orderDto.setRazorpayOrderId(order.getRazorpayOrderId());
        orderDto.setRazorpayPaymentId(order.getRazorpayPaymentId());
        orderDto.setPaidAt(order.getPaidAt());
        orderDto.setPlanStartDate(order.getPlanStartDate());
        orderDto.setPlanEndDate(order.getPlanEndDate());
        orderDto.setOrderStatus(order.getOrderStatus());

        return orderDto;
    }
}
