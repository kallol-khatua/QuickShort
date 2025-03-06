package com.quickshort.payment.controllers;

import com.quickshort.common.dto.SuccessApiResponse;
import com.quickshort.payment.dto.OrderDto;
import com.quickshort.payment.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/v1/orders")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;


    @PostMapping({"/", ""})
    public ResponseEntity<SuccessApiResponse<OrderDto>> createOrder(@RequestBody(required = false) OrderDto orderDto) {

        if (orderDto == null) {
            orderDto = new OrderDto();
        }

        OrderDto createdOrder = orderService.createOrder(orderDto);

        // Set up response
        SuccessApiResponse<OrderDto> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.CREATED.value());
        response.setStatus_text(HttpStatus.CREATED.name());
        response.setSuccess(true);
        response.setStatus("Order Created");
        response.setMessage("New order created");
        response.setData(createdOrder);

        // Return the response with 201 Created status
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // Verify payment
    @PostMapping({"/verify-payment/", "/verify-payment"})
    public ResponseEntity<SuccessApiResponse<OrderDto>> verifyPayment(
            @RequestParam(required = false) String paymentId,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String signature
    ) {
        // paymentId, orderId, signature
        OrderDto orderDto = orderService.verifyPayment(paymentId, orderId, signature);

        // Set up response
        SuccessApiResponse<OrderDto> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Order Verified");
        response.setMessage("Order status verified");
        response.setData(orderDto);

        // Return the response with status
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Get all orders for a workspace
    @GetMapping(value = {"/{workspaceId}/all-orders", "/{workspaceId}/all-orders/"})
    public ResponseEntity<SuccessApiResponse<List<OrderDto>>> getAllOrders(@PathVariable UUID workspaceId) {

        List<OrderDto> orderDtos = orderService.getAllOrders(workspaceId);

        // Set up response
        SuccessApiResponse<List<OrderDto>> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Order found");
        response.setMessage("Order details found");
        response.setData(orderDtos);

        // Return the response with status
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // TODO: Cancel order


    // create repay order
    @PostMapping({"/repay/", "/repay"})
    public ResponseEntity<SuccessApiResponse<OrderDto>> createRepayOrder(@RequestBody(required = false) OrderDto orderDto) {
        if (orderDto == null) {
            orderDto = new OrderDto();
        }

        OrderDto createdOrder = orderService.createRepayOrder(orderDto);

        // Set up response
        SuccessApiResponse<OrderDto> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.CREATED.value());
        response.setStatus_text(HttpStatus.CREATED.name());
        response.setSuccess(true);
        response.setStatus("Order Created");
        response.setMessage("Repay order created");
        response.setData(createdOrder);

        // Return the response with 201 Created status
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
