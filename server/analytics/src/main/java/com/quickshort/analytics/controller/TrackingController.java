package com.quickshort.analytics.controller;

import com.quickshort.analytics.dto.ShortUrlDto;
import com.quickshort.analytics.model.ShortUrl;
import com.quickshort.analytics.service.ShortUrlService;
import com.quickshort.common.dto.SuccessApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
}
