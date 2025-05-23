package com.quickshort.payment.service.impl;

import com.quickshort.common.enums.WorkspaceStatus;
import com.quickshort.common.enums.WorkspaceType;
import com.quickshort.common.events.WorkspaceTypeUpgradationEvent;
import com.quickshort.common.exception.*;
import com.quickshort.common.payload.WorkspacePayload;
import com.quickshort.payment.dto.OrderDto;
import com.quickshort.payment.enums.OrderStatus;
import com.quickshort.payment.kafka.producers.WorkspaceTypeUpgradationProducer;
import com.quickshort.payment.kafka.producers.WorkspaceUpdationProducer;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Autowired
    private WorkspaceTypeUpgradationProducer workspaceTypeUpgradationProducer;

    @Autowired
    private WorkspaceUpdationProducer workspaceUpdationProducer;

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


            // Allow to upgrade only for free type
            if (workspace.getType() != WorkspaceType.FREE) {
                errors.add(new FieldError("Already Upgraded To Premium Plans", "workspace_id"));
                throw new MethodNotAllowedException("Already Upgraded To Premium Plans", "Renew Subscription", errors);
            }


            // If order is already created when status is awaiting payment then return the already created order
            Optional<Order> existingOrder = orderRepository.findByWorkspaceIdAndOrderStatus(workspace, OrderStatus.AWAITING_PAYMENT);
            if (existingOrder.isPresent()) {
                LOGGER.info("Order already created");
                return OrderMapper.maptoOrderDto(existingOrder.get());
            }


            // Create razorpay order
            String razorpayOrderId = razorpayService.createOrder(plan.getAmount(), workspace.getId().toString());
            Order newOrder = new Order();
            newOrder.setPlanId(plan);
            newOrder.setAmount(plan.getAmount());
            newOrder.setRazorpayOrderId(razorpayOrderId);
            newOrder.setWorkspaceId(workspace);
            newOrder.setOrderStatus(OrderStatus.AWAITING_PAYMENT);

            Order savedOrder = orderRepository.save(newOrder);


            // Return the order
            return OrderMapper.maptoOrderDto(savedOrder);
        } catch (BadRequestException | ForbiddenException | MethodNotAllowedException exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error while creating order", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }


    // Verify payment, update workspace details, update order, and send to kafka
    @Transactional
    @Override
    public OrderDto verifyPayment(String paymentId, String orderId, String signature) {
        try {
            List<FieldError> errors = new ArrayList<>();
            // Check all data present or not
            if (orderId == null || orderId.isEmpty()) {
                errors.add(new FieldError("Order id is required", "order_id"));
            }
            if (paymentId == null || paymentId.isEmpty()) {
                errors.add(new FieldError("Payment id is required", "payment_id"));
            }
            if (signature == null || signature.isEmpty()) {
                errors.add(new FieldError("Signature is required", "signature"));
            }
            if (!errors.isEmpty()) {
                throw new BadRequestException("Invalid Data Provided", "Please fill all the details", errors);
            }


            // Check order exit or not
            Optional<Order> existingOrder = orderRepository.findByRazorpayOrderId(orderId);
            if (existingOrder.isEmpty()) {
                errors.add(new FieldError("No order found", "razorpay_order_id"));
                throw new BadRequestException("Invalid Data Provided", "No order found for the id", errors);
            }
            Order order = existingOrder.get();


            // Verify payment
            boolean isValid = razorpayService.verifyPayment(paymentId, orderId, signature);

            // If successfully verified then store details to DB, update workspace, and emit event to kafka
            if (isValid) {
                // Change status to completed
                order.setOrderStatus(OrderStatus.COMPLETED);


                // update workspace
                Optional<Workspace> existingWorkspace = workspaceRepository.findById(order.getWorkspaceId().getId());
                if (existingWorkspace.isEmpty()) {
                    errors.add(new FieldError("No workspace found", "workspace_id"));
                    throw new BadRequestException("Invalid Data Provided", "No workspace found for the id", errors);
                }
                Workspace workspace = existingWorkspace.get();

                Optional<Plan> existingPlan = planRepository.findById(order.getPlanId().getId());
                if (existingPlan.isEmpty()) {
                    errors.add(new FieldError("No plan found", "plan_id"));
                    throw new BadRequestException("Invalid Data Provided", "No plan found for the id", errors);
                }
                Plan plan = existingPlan.get();

                workspace.setType(plan.getWorkspaceType());
                workspace.setLinkCreationLimitPerMonth(plan.getLinkCreationLimitPerMonth());
                workspace.setMemberLimit(plan.getMemberLimit());
                workspace.setWorkspaceStatus(WorkspaceStatus.ACTIVE);

                LocalDate currentDate = LocalDate.now();
                workspace.setLastResetDate(currentDate);

                // After one month reset link created count
                // if curr date is 2025-02-26 then next reset date will be 2025-03-26
                workspace.setNextResetDate(currentDate.plusMonths(1));

                // After the plan duration end
                // if curr date is 2025-02-26 and plan duration is 3 month then next billing date will be 2025-05-26
                workspace.setNextBillingDate(currentDate.plusMonths(plan.getPlanDurationMonth()));

                Workspace upgradedWorkspace = workspaceRepository.save(workspace);


                // Update existing order
                order.setRazorpayPaymentId(paymentId);
                LocalDateTime now = LocalDateTime.now();
                order.setPaidAt(now);
                order.setPlanStartDate(currentDate);
                // last date of for the plan duration
                // if curr date is 2025-02-26 and plan duration is 3 month then next end date will be 2025-05-25
                order.setPlanEndDate(currentDate.plusMonths(plan.getPlanDurationMonth()).minusDays(1));
                order.setOrderStatus(OrderStatus.COMPLETED);
                Order updatedOrder = orderRepository.save(order);


                // Send updated workspace to kafka
                WorkspaceTypeUpgradationEvent event = new WorkspaceTypeUpgradationEvent();
                event.setKey(upgradedWorkspace.getId().toString());
                event.setMessage("Workspace upgraded");
                event.setStatus("Workspace Upgraded");
                // Set payload
                WorkspacePayload payload = getWorkspacePayload(upgradedWorkspace);
                event.setWorkspacePayload(payload);

                workspaceTypeUpgradationProducer.workspaceTypeUpgradationMessage(event.getKey(), event);


                return OrderMapper.maptoOrderDto(updatedOrder);
            } else {
                // Change status to failed
                order.setOrderStatus(OrderStatus.PAYMENT_FAILED);
                Order updatedOrder = orderRepository.save(order);

                return OrderMapper.maptoOrderDto(updatedOrder);
            }
        } catch (BadRequestException | ForbiddenException exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error while creating order", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }


    // All orders
    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders(UUID workspaceId) {
        try {
            List<FieldError> errors = new ArrayList<>();

            // Check workspace exist or not
            Optional<Workspace> existingWorkspace = workspaceRepository.findById(workspaceId);
            if (existingWorkspace.isEmpty()) {
                errors.add(new FieldError("No workspace found", "workspace_id"));
                throw new BadRequestException("Invalid Data Provided", "No workspace found", errors);
            }
            Workspace workspace = existingWorkspace.get();


            List<Order> orders = orderRepository.findByWorkspaceId(workspace, Sort.by("createdAt").descending());

            return orders.stream()
                    .map(order -> new OrderDto(
                            order.getId(),
                            order.getPlanId().getId(),
                            order.getAmount(),
                            order.getRazorpayOrderId(),
                            order.getRazorpayPaymentId(),
                            order.getWorkspaceId().getId(),
                            order.getPaidAt(),
                            order.getPlanStartDate(),
                            order.getPlanEndDate(),
                            order.getOrderStatus()
                    ))
                    .collect(Collectors.toList());
        } catch (BadRequestException | ForbiddenException exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error while creating order", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }


    // Create repay order if workspace is active, premium
    @Transactional
    @Override
    public OrderDto createRepayOrder(OrderDto orderDto) {
        try {
            List<FieldError> errors = new ArrayList<>();
            if (orderDto.getWorkspaceId() == null || orderDto.getWorkspaceId().toString().isEmpty()) {
                errors.add(new FieldError("Workspace id is required", "workspace_id"));
            }
            if (orderDto.getPlanId() == null || orderDto.getPlanId().toString().isEmpty()) {
                errors.add(new FieldError("Plan id is required", "plan_id"));
            }
            if (!errors.isEmpty()) {
                throw new BadRequestException("Invalid Data Provided", "Please fill all the details", errors);
            }


            // Check plan exist or not, if not then throw error
            Optional<Plan> existingPlan = planRepository.findById(orderDto.getPlanId());
            if (existingPlan.isEmpty()) {
                errors.add(new FieldError("No plan found", "plan_id"));
                throw new BadRequestException("Invalid Data Provided", "No plan found.", errors);
            }
            Plan plan = existingPlan.get();


            // Check workspace exist or not, if not exist then throw error
            Optional<Workspace> existingWorkspace = workspaceRepository.findById(orderDto.getWorkspaceId());
            if (existingWorkspace.isEmpty()) {
                errors.add(new FieldError("No workspace found", "workspace_id"));
                throw new BadRequestException("Invalid Data Provided", "No workspace found.", errors);
            }
            Workspace workspace = existingWorkspace.get();


            // Allow order creation only when workspace type is same as the plan type
            // If not same then throw error
            if (workspace.getType() != plan.getWorkspaceType()) {
                errors.add(new FieldError("Can not create order", "workspace_id"));
                throw new MethodNotAllowedException("Can not create order", "Can not create order", errors);
            }


            // If order is already created when status is awaiting payment then return the already created order
            Optional<Order> existingOrder = orderRepository.findByWorkspaceIdAndOrderStatus(workspace, OrderStatus.AWAITING_PAYMENT);
            if (existingOrder.isPresent()) {
                LOGGER.info("Order already exist");
                return OrderMapper.maptoOrderDto(existingOrder.get());
            }


            // Create razorpay order
            String razorpayOrderId = razorpayService.createOrder(plan.getAmount(), workspace.getId().toString());
            Order newOrder = new Order();
            newOrder.setPlanId(plan);
            newOrder.setAmount(plan.getAmount());
            newOrder.setRazorpayOrderId(razorpayOrderId);
            newOrder.setWorkspaceId(workspace);
            newOrder.setOrderStatus(OrderStatus.AWAITING_PAYMENT);


            // save order to DB
            Order savedOrder = orderRepository.save(newOrder);


            // Return the order
            return OrderMapper.maptoOrderDto(savedOrder);
        } catch (BadRequestException | ForbiddenException | MethodNotAllowedException exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error while creating order", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }

    // cancel order
    @Override
    @Transactional
    public OrderDto cancelOrder(UUID orderId) {
        try {
            List<FieldError> errors = new ArrayList<>();

            // If order not found throw error
            Optional<Order> existingOrder = orderRepository.findById(orderId);
            if (existingOrder.isEmpty()) {
                errors.add(new FieldError("Order not found", "order_id"));
                throw new BadRequestException("Invalid Data Provided", "Order not found.", errors);
            }


            // if OrderStatus != AWAITING_PAYMENT then do not allow to cancel order
            Order order = existingOrder.get();
            if (order.getOrderStatus() != OrderStatus.AWAITING_PAYMENT) {
                errors.add(new FieldError("Not allowed to cancel order", "order_id"));
                throw new BadRequestException("Invalid Data Provided", "Not allowed to cancel order.", errors);
            }


            // update status to cancelled and save to DB
            order.setOrderStatus(OrderStatus.CANCELLED);
            Order updatedOrder = orderRepository.save(order);


            // Return the order
            return OrderMapper.maptoOrderDto(updatedOrder);
        } catch (BadRequestException | ForbiddenException | MethodNotAllowedException exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error while creating order", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }

    // Function to get workspace payload from upgraded workspace
    private WorkspacePayload getWorkspacePayload(Workspace upgradedWorkspace) {
        WorkspacePayload payload = new WorkspacePayload();

        payload.setId(upgradedWorkspace.getId());
        payload.setType(upgradedWorkspace.getType());
        payload.setCreatedAt(upgradedWorkspace.getCreatedAt());
        payload.setUpdatedAt(upgradedWorkspace.getUpdatedAt());

        payload.setLinkCreationLimitPerMonth(upgradedWorkspace.getLinkCreationLimitPerMonth());
        payload.setMemberLimit(upgradedWorkspace.getMemberLimit());

        payload.setLastResetDate(upgradedWorkspace.getLastResetDate());
        payload.setNextResetDate(upgradedWorkspace.getNextResetDate());
        payload.setNextBillingDate(upgradedWorkspace.getNextBillingDate());

        payload.setWorkspaceStatus(upgradedWorkspace.getWorkspaceStatus());

        return payload;
    }

    // Verify payment, update workspace details, update order, and send to kafka
    @Transactional
    @Override
    public OrderDto verifyRepayPayment(String paymentId, String orderId, String signature) {
        try {
            List<FieldError> errors = new ArrayList<>();
            // Check all data present or not
            if (orderId == null || orderId.isEmpty()) {
                errors.add(new FieldError("Order id is required", "order_id"));
            }
            if (paymentId == null || paymentId.isEmpty()) {
                errors.add(new FieldError("Payment id is required", "payment_id"));
            }
            if (signature == null || signature.isEmpty()) {
                errors.add(new FieldError("Signature is required", "signature"));
            }
            if (!errors.isEmpty()) {
                throw new BadRequestException("Invalid Data Provided", "Please fill all the details", errors);
            }


            // Check order exit or not
            Optional<Order> existingOrder = orderRepository.findByRazorpayOrderId(orderId);
            if (existingOrder.isEmpty()) {
                errors.add(new FieldError("No order found", "razorpay_order_id"));
                throw new BadRequestException("Invalid Data Provided", "No order found for the id", errors);
            }
            Order order = existingOrder.get();


            // Verify payment
            boolean isValid = razorpayService.verifyPayment(paymentId, orderId, signature);

            // If successfully verified then store details to DB, update workspace, and emit event to kafka
            if (isValid) {

                // update workspace
                Optional<Workspace> existingWorkspace = workspaceRepository.findById(order.getWorkspaceId().getId());
                if (existingWorkspace.isEmpty()) {
                    errors.add(new FieldError("No workspace found", "workspace_id"));
                    throw new BadRequestException("Invalid Data Provided", "No workspace found for the id", errors);
                }
                Workspace workspace = existingWorkspace.get();

                Optional<Plan> existingPlan = planRepository.findById(order.getPlanId().getId());
                if (existingPlan.isEmpty()) {
                    errors.add(new FieldError("No plan found", "plan_id"));
                    throw new BadRequestException("Invalid Data Provided", "No plan found for the id", errors);
                }
                Plan plan = existingPlan.get();


                if (workspace.getWorkspaceStatus() == WorkspaceStatus.ACTIVE) {
                    LOGGER.info("Verifying payment for active workspace -> {}", workspace.getId());
                    // if workspace is active then bill start after end of the current date
                    LocalDate nextBillingDate = workspace.getNextBillingDate();

                    // After the plan duration end
                    // if nextBillingDate is 2025-04-15 and plan duration is 3 month then next billing date will be 2025-07-15
                    workspace.setNextBillingDate(nextBillingDate.plusMonths(plan.getPlanDurationMonth()));

                    // Update existing order
                    order.setRazorpayPaymentId(paymentId);
                    LocalDateTime now = LocalDateTime.now();
                    order.setPaidAt(now);
                    order.setPlanStartDate(nextBillingDate);
                    // last date of for the plan duration
                    // if curr date is 2025-04-15 and plan duration is 3 month then next end date will be 2025-07-14
                    order.setPlanEndDate(nextBillingDate.plusMonths(plan.getPlanDurationMonth()).minusDays(1));
                    order.setOrderStatus(OrderStatus.COMPLETED);

                } else {
                    // if not active then start from current date
                    LOGGER.info("Verifying payment for not active workspace -> {}", workspace.getId());
                    workspace.setWorkspaceStatus(WorkspaceStatus.ACTIVE);

                    LocalDate currentDate = LocalDate.now();
                    // eg: current date 2025-04-15
                    workspace.setLastResetDate(currentDate);

                    // After one month reset link created count
                    // if curr date is 2025-04-15 then next reset date will be 2025-05-15
                    workspace.setNextResetDate(currentDate.plusMonths(1));

                    // After the plan duration end
                    // if curr date is 2025-04-15 and plan duration is 3 month then next billing date will be 2025-07-15
                    workspace.setNextBillingDate(currentDate.plusMonths(plan.getPlanDurationMonth()));

                    // Update existing order
                    order.setRazorpayPaymentId(paymentId);
                    LocalDateTime now = LocalDateTime.now();
                    order.setPaidAt(now);
                    order.setPlanStartDate(currentDate);
                    // last date of for the plan duration
                    // if curr date is 2025-04-15 and plan duration is 3 month then next end date will be 2025-07-14
                    order.setPlanEndDate(currentDate.plusMonths(plan.getPlanDurationMonth()).minusDays(1));
                    order.setOrderStatus(OrderStatus.COMPLETED);
                }


                Workspace updatedWorkspace = workspaceRepository.save(workspace);
                Order updatedOrder = orderRepository.save(order);

                // Send updated workspace to kafka
                WorkspaceTypeUpgradationEvent event = new WorkspaceTypeUpgradationEvent();
                event.setKey(updatedWorkspace.getId().toString());
                event.setMessage("Workspace updated");
                event.setStatus("Workspace updated");
                // Set payload
                WorkspacePayload payload = getWorkspacePayload(updatedWorkspace);
                event.setWorkspacePayload(payload);

                workspaceUpdationProducer.workspaceUpdationMessage(event.getKey(), event);


                return OrderMapper.maptoOrderDto(updatedOrder);
            } else {
                // Change status to failed
                LOGGER.info("Payment verification failed -> {}", order.getId());
                order.setOrderStatus(OrderStatus.PAYMENT_FAILED);
                Order updatedOrder = orderRepository.save(order);

                return OrderMapper.maptoOrderDto(updatedOrder);
            }
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
