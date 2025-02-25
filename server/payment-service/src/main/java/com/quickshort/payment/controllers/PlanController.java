package com.quickshort.payment.controllers;

import com.quickshort.common.dto.SuccessApiResponse;
import com.quickshort.payment.models.Plan;
import com.quickshort.payment.service.PlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/v1/plan")
public class PlanController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlanController.class);

    @Autowired
    private PlanService planService;

    @PostMapping({"/", ""})
    public ResponseEntity<SuccessApiResponse<Plan>> createPlan() {
        // Create plan
        Plan createdPlan = planService.cratePlan();

        LOGGER.info("New plan created -> {}", createdPlan);

        // Set up response
        SuccessApiResponse<Plan> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.CREATED.value());
        response.setStatus_text(HttpStatus.CREATED.name());
        response.setSuccess(true);
        response.setStatus("Plan Created");
        response.setMessage("New plan created");
        response.setData(createdPlan);

        // Return the response with 201 Created status
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
