package com.quickshort.payment.service.impl;

import com.quickshort.common.enums.WorkspaceType;
import com.quickshort.common.exception.BadRequestException;
import com.quickshort.common.exception.FieldError;
import com.quickshort.common.exception.ForbiddenException;
import com.quickshort.common.exception.InternalServerErrorException;
import com.quickshort.payment.enums.PlanDuration;
import com.quickshort.payment.models.Plan;
import com.quickshort.payment.repository.PlanRepository;
import com.quickshort.payment.service.PlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlanServiceImpl.class);

    @Autowired
    private PlanRepository planRepository;

    @Override
    public Plan cratePlan() {
        try {
            Plan newPlan = new Plan();
            newPlan.setWorkspaceType(WorkspaceType.PRO);
            newPlan.setMemberLimit(10);
            newPlan.setLinkCreationLimitPerMonth(1000);

            newPlan.setAmount(1000.00);
            newPlan.setAmountPerMonth(1000.00);
            newPlan.setPlanDuration(PlanDuration.MONTHLY);

            newPlan.setPlanDurationMonth(1);
            newPlan.setPercentageOff(0.00);


            return planRepository.save(newPlan);
        } catch (BadRequestException | ForbiddenException exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during plan creation ", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }

    @Override
    public List<Plan> getAllPlan() {
        try {
            return planRepository.findAll();
        } catch (Exception e) {
            LOGGER.error("Unexpected error during finding all plans ", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }
}
