package com.quickshort.payment.service.impl;

import com.quickshort.common.exception.BadRequestException;
import com.quickshort.common.exception.FieldError;
import com.quickshort.common.exception.ForbiddenException;
import com.quickshort.common.exception.InternalServerErrorException;
import com.quickshort.payment.dto.OrderDto;
import com.quickshort.payment.enums.OrderStatus;
import com.quickshort.payment.mappers.OrderMapper;
import com.quickshort.payment.models.Order;
import com.quickshort.payment.models.Plan;
import com.quickshort.payment.models.Workspace;
import com.quickshort.payment.repository.OrderRepository;
import com.quickshort.payment.repository.PlanRepository;
import com.quickshort.payment.repository.WorkspaceRepository;
import com.quickshort.payment.service.OrderService;
import com.quickshort.payment.service.RazorpayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private RazorpayService razorpayService;

    // Create razorpay order, or return already created one
    @Transactional
    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        try {
            List<FieldError> errors = new ArrayList<>();
            if (orderDto.getWorkspaceId() == null) {
                errors.add(new FieldError("Workspace id is required", "workspace_id"));
            }
            if (orderDto.getPlanId() == null) {
                errors.add(new FieldError("Plan id is required", "plan_id"));
            }
            if (!errors.isEmpty()) {
                throw new BadRequestException("Invalid Data Provided", "Please fill all the details", errors);
            }


            // Check plan exist or not
            Optional<Plan> existingPlan = planRepository.findById(orderDto.getPlanId());
            if (existingPlan.isEmpty()) {
                errors.add(new FieldError("No plan found", "plan_id"));
                throw new BadRequestException("Invalid Data Provided", "No plan found for the id", errors);
            }
            Plan plan = existingPlan.get();


            // Check workspace exist or not
            Optional<Workspace> existingWorkspace = workspaceRepository.findById(orderDto.getWorkspaceId());
            if (existingWorkspace.isEmpty()) {
                errors.add(new FieldError("No workspace found", "workspace_id"));
                throw new BadRequestException("Invalid Data Provided", "No workspace found for the id", errors);
            }
            Workspace workspace = existingWorkspace.get();


            // If order is already created when status is awaiting payment then return the already created order
            Optional<Order> existingOrder = orderRepository.findByWorkspaceIdAndOrderStatus(workspace, OrderStatus.AWAITING_PAYMENT);
            if (existingOrder.isPresent()) {
                return OrderMapper.maptoOrderDto(existingOrder.get());
            }


            // TODO: Add receipt
            // Create razorpay order
            String razorpayOrderId = razorpayService.createOrder(plan.getAmount());
            Order newOrder = new Order();
            newOrder.setPlanId(plan);
            newOrder.setAmount(plan.getAmount());
            newOrder.setRazorpayOrderId(razorpayOrderId);
            newOrder.setWorkspaceId(workspace);
            newOrder.setOrderStatus(OrderStatus.AWAITING_PAYMENT);

            Order savedOrder = orderRepository.save(newOrder);


            // Return the order
            return OrderMapper.maptoOrderDto(savedOrder);
        } catch (BadRequestException | ForbiddenException exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error while creating order", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }

    // Verify payment
    @Transactional
    @Override
    public OrderDto verifyPayment(String paymentId, String orderId, String signature) {
        try {
            Optional<Order> existingOrder = orderRepository.findById("KJDJDJ");

            boolean isValid = razorpayService.verifyPayment(paymentId, orderId, signature);

            if (isValid) {

            } else {

            }

            return null;
        } catch (BadRequestException | ForbiddenException exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error while creating order", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }
}
