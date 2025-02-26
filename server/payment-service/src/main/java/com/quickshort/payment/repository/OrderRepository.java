package com.quickshort.payment.repository;

import com.quickshort.payment.enums.OrderStatus;
import com.quickshort.payment.models.Order;
import com.quickshort.payment.models.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByWorkspaceIdAndOrderStatus(Workspace workspaceId, OrderStatus orderStatus);
}
