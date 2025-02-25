package com.quickshort.payment.service;

import com.quickshort.payment.models.Plan;

import java.util.List;

public interface PlanService {
    Plan cratePlan();
    List<Plan> getAllPlan();
}
