package com.quickshort.email.controller;

import com.quickshort.common.dto.SuccessApiResponse;
import com.quickshort.common.exception.BadRequestException;
import com.quickshort.common.exception.FieldError;
import com.quickshort.common.exception.InternalServerErrorException;
import com.quickshort.email.dto.AddMemberDto;
import com.quickshort.email.service.AddMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/v1/email")
public class AddMemberController {

    @Autowired
    private AddMemberService addMember;

    @PostMapping("/{workspaceId}/add")
    public ResponseEntity<SuccessApiResponse<?>> sendAddMemberEmail(@PathVariable String workspaceId, @RequestBody AddMemberDto memberDto) {

        if (memberDto.getEmail() == null || memberDto.getEmail().trim().isEmpty()) {
            List<FieldError> errorList = new ArrayList<>();
            errorList.add(new FieldError("Email not provided", "email"));
            throw new BadRequestException("Email not specified", "Please provide email", errorList);
        }

        try {
            addMember.sendEmail(memberDto.getEmail(), memberDto.getMemberType(), workspaceId);

            // Set up response
            SuccessApiResponse<?> response = new SuccessApiResponse<>();
            response.setStatus_code(HttpStatus.OK.value());
            response.setStatus_text(HttpStatus.OK.name());
            response.setSuccess(true);
            response.setStatus("Email send");
            response.setMessage("Invitation email send successfully");

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            List<FieldError> errorList = new ArrayList<>();
            errorList.add(new FieldError("Internal server error"));

            throw new InternalServerErrorException("Internal server error", "internal Server Error", errorList);
        }
    }
}
