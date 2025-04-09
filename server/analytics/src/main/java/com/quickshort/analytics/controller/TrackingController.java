package com.quickshort.analytics.controller;

import com.quickshort.analytics.dto.ClickAnalyticsResponse;
import com.quickshort.analytics.dto.ShortUrlDto;
import com.quickshort.analytics.model.ShortUrl;
import com.quickshort.analytics.service.ShortUrlService;
import com.quickshort.common.dto.SuccessApiResponse;
import com.quickshort.common.exception.BadRequestException;
import com.quickshort.common.exception.FieldError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/v1/analytics")
public class TrackingController {

    @Autowired
    private ShortUrlService shortUrlService;

    @GetMapping(value = "/{shortCode}")
    public ResponseEntity<SuccessApiResponse<ShortUrlDto>> getOriginalUrl(HttpServletRequest request, @PathVariable String shortCode) {

        ShortUrlDto originalUrl = shortUrlService.getOriginalUrl(request, shortCode);

        // Set up response
        SuccessApiResponse<ShortUrlDto> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Shorten URL Found");
        response.setMessage("Shorten URL found successfully");
        response.setData(originalUrl);

        // Return the response with 200 Created status
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/report/{workspaceId}")
    public ResponseEntity<SuccessApiResponse<ClickAnalyticsResponse>> getAnalysis(
            @PathVariable(required = false) String workspaceId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
//        Checking workspace id is provided or not
        if (workspaceId == null || workspaceId.isBlank()) {
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Missing required query parameter: 'workspaceId'", "workspaceId"));
            throw new BadRequestException("All filed not provided", "Missing required query parameter: 'workspaceId'", errors);
        }

//        check workspace id is a valid uuid or not
        UUID workspaceUUID;
        try {
            workspaceUUID = UUID.fromString(workspaceId);
        } catch (IllegalArgumentException e) {
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Invalid workspaceId: must be a valid UUID", "workspaceId"));
            throw new BadRequestException("Invalid format", "Invalid workspaceId: must be a valid UUID", errors);
        }

//        from date must be present
        if (from == null || from.isBlank()) {
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Missing required query parameter: 'from'", "from"));
            throw new BadRequestException("All filed not provided", "Missing required query parameter: 'from'", errors);
        }

//        to date must be present
        if (to == null || to.isBlank()) {
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Missing required query parameter: 'to'", "to"));
            throw new BadRequestException("All filed not provided", "Missing required query parameter: 'to'", errors);
        }

        // Validate format
        LocalDate fromDate;
        LocalDate toDate;
        try {
            fromDate = LocalDate.parse(from); // Expects yyyy-MM-dd
            toDate = LocalDate.parse(to);
        } catch (DateTimeParseException e) {
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Date format must be yyyy-MM-dd", "date"));
            throw new BadRequestException("All filed not provided", "Date format must be yyyy-MM-dd", errors);
        }

//        check from date must be before to date
        if (fromDate.isAfter(toDate)) {
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Choose correct date"));
            throw new BadRequestException("All filed not provided", "Missing required query parameter: 'to'", errors);
        }

//        set from date time to min 00:00
        LocalDateTime fromDateAndTime = fromDate.atTime(LocalTime.MIN);
//        set to date time to max 23:59
        LocalDateTime toDateAndTime = toDate.atTime(LocalTime.MAX);

        ClickAnalyticsResponse report = shortUrlService.getReport(workspaceUUID, fromDateAndTime, toDateAndTime);

        // Set up response
        SuccessApiResponse<ClickAnalyticsResponse> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Report is ready");
        response.setMessage("Report is ready");
        response.setData(report);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
