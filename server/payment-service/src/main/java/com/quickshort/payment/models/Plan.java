package com.quickshort.payment.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.quickshort.common.enums.WorkspaceType;
import com.quickshort.payment.enums.PlanDuration;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "plans")
public class Plan {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "workspace_type", nullable = false)
    private WorkspaceType workspaceType; // PRO, BUSINESS

    @Column(name = "member_limit", nullable = false)
    private int memberLimit;

    @Column(name = "link_creation_limit_per_month", nullable = false)
    private int linkCreationLimitPerMonth;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "amount_per_month", nullable = false)
    private double amountPerMonth;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_duration", nullable = false)
    private PlanDuration planDuration;

    @Column(name = "plan_duration_month", nullable = false)
    private int planDurationMonth;

    @Column(name = "percentage_off", nullable = false)
    private double percentageOff;

    @JsonManagedReference
    @OneToMany(mappedBy = "planId")
    @JsonIgnore
    private List<Order> orders;
}
