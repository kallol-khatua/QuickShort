package com.quickshort.payment.dto;

import com.quickshort.payment.enums.OrderStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {
    private UUID id;
    private UUID planId;
    private double amount;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private UUID workspaceId;
    private LocalDateTime paidAt;
    private LocalDate planStartDate;
    private LocalDate planEndDate;
    private OrderStatus orderStatus;
}
