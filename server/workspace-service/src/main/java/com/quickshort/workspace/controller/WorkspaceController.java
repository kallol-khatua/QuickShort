package com.quickshort.workspace.controller;

import com.quickshort.common.dto.SuccessApiResponse;
import com.quickshort.workspace.dto.ShortUrlDto;
import com.quickshort.workspace.dto.WorkspaceDto;

import com.quickshort.workspace.dto.WorkspaceMemberDto;
import com.quickshort.workspace.models.ShortUrl;
import com.quickshort.workspace.service.ShortUrlService;
import com.quickshort.workspace.service.WorkspaceMemberService;
import com.quickshort.workspace.service.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/api/v1/workspace")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @Autowired
    private WorkspaceMemberService workspaceMemberService;

    @Autowired
    private ShortUrlService shortUrlService;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceController.class);

    // Create new workspace with member type = owner
    @PostMapping({"/", ""})
    public ResponseEntity<SuccessApiResponse<WorkspaceDto>> createWorkspace(@RequestBody(required = false) WorkspaceDto workspaceDto) {

        if (workspaceDto == null) {
            workspaceDto = new WorkspaceDto();
        }

        // create workspace
        WorkspaceDto createdDto = workspaceService.createWorkspace(workspaceDto);

        // Set up response
        SuccessApiResponse<WorkspaceDto> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.CREATED.value());
        response.setStatus_text(HttpStatus.CREATED.name());
        response.setSuccess(true);
        response.setStatus("Workspace Created");
        response.setMessage("New workspace created");
        response.setData(createdDto);

        // Return the response with 201 Created status
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // Get all the workspace where a user is either member or an owner
    @GetMapping({"/", ""})
    public ResponseEntity<SuccessApiResponse<List<WorkspaceMemberDto>>> allWorkspace() {

        // Find all the workspace using workspace member entity, where the user is either owner or member
        List<WorkspaceMemberDto> allWorkspaces = workspaceMemberService.allWorkspaceWhereUserIsMemberOrOwner();
        LOGGER.info("Workspace member list found where user is owner or member");

        // Set up response
        SuccessApiResponse<List<WorkspaceMemberDto>> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Workspaces found");
        response.setMessage("All workspaces found");
        response.setData(allWorkspaces);

        // Return the response with 200 status
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Get all the workspace where a user is an owner
    @GetMapping({"/owner", "/owner/"})
    public ResponseEntity<SuccessApiResponse<List<WorkspaceMemberDto>>> allWorkspaceWhereUserIsOwner() {

        // Find all the workspace using workspace member entity, where the user is an owner
        List<WorkspaceMemberDto> allWorkspacesWhereOwner = workspaceMemberService.allWorkspaceWhereUserIsOwner();

        // Set up response
        SuccessApiResponse<List<WorkspaceMemberDto>> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Workspaces found");
        response.setMessage("All workspaces found");
        response.setData(allWorkspacesWhereOwner);

        // Return the response with 200 status
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Get all the workspace where a user is a member
    @GetMapping({"/member", "/member/"})
    public ResponseEntity<SuccessApiResponse<List<WorkspaceMemberDto>>> allWorkspaceWhereUserIsMember() {

        // Find all the workspace using workspace member entity, where the user is a member
        List<WorkspaceMemberDto> allWorkspacesWhereMember = workspaceMemberService.allWorkspaceWhereUserIsMember();

        // Set up response
        SuccessApiResponse<List<WorkspaceMemberDto>> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Workspaces found");
        response.setMessage("All workspaces found");
        response.setData(allWorkspacesWhereMember);

        // Return the response with 200 status
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Create short url
    @PostMapping(value = {"/{workspaceId}/shorten-url", "/{workspaceId}/shorten-url/"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuccessApiResponse<ShortUrlDto>> createShortUrl(@PathVariable UUID workspaceId, @RequestBody(required = false) ShortUrlDto shortUrlDto) {

        if (shortUrlDto == null) {
            shortUrlDto = new ShortUrlDto();
        }

        // Create short url
        ShortUrlDto createdUrl = shortUrlService.generateShortUrl(workspaceId, shortUrlDto);
        LOGGER.info("Short URL created -> {}", createdUrl.getId().toString());

        // Set up response
        SuccessApiResponse<ShortUrlDto> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.CREATED.value());
        response.setStatus_text(HttpStatus.CREATED.name());
        response.setSuccess(true);
        response.setStatus("Shorten URL Created");
        response.setMessage("Shorten URL created successfully");
        response.setData(createdUrl);

        // Return the response with 201 Created status
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // Find short urls
    @GetMapping(value = {"/{workspaceId}/shorten-url", "/{workspaceId}/shorten-url/"})
    public ResponseEntity<SuccessApiResponse<Page<ShortUrl>>> findAllUrl(@PathVariable UUID workspaceId,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {

        Page<ShortUrl> shortUrlDtoList = shortUrlService.getAllUrl(workspaceId, page, size);

        // Set up response
        SuccessApiResponse<Page<ShortUrl>> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Shorten URL Found");
        response.setMessage("Shorten URL found successfully");
        response.setData(shortUrlDtoList);

        // Return the response with 200 Created status
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Apply to join as a member
    @PostMapping(value = {"/{workspaceId}/join-as-member", "/{workspaceId}/join-as-member/"})
    public ResponseEntity<SuccessApiResponse<WorkspaceMemberDto>> joinWorkspaceAsMember(@PathVariable UUID workspaceId) {

        // Join as Member
        WorkspaceMemberDto joinedMember = workspaceMemberService.joinAsMember(workspaceId);

        // Set up response
        SuccessApiResponse<WorkspaceMemberDto> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Applied To Be A Member");
        response.setMessage("Successfully applied to be a member");
        response.setData(joinedMember);

        // Return the response with 200 Created status
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Apply to join as an owner
    @PostMapping(value = {"/{workspaceId}/join-as-owner", "/{workspaceId}/join-as-owner/"})
    public ResponseEntity<SuccessApiResponse<WorkspaceMemberDto>> joinWorkspaceAsOwner(@PathVariable UUID workspaceId) {

        // Join as Member
        WorkspaceMemberDto joinedMember = workspaceMemberService.joinAsOwner(workspaceId);

        // Set up response
        SuccessApiResponse<WorkspaceMemberDto> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Applied To Be An Owner");
        response.setMessage("Successfully applied to be an owner");
        response.setData(joinedMember);

        // Return the response with 200 Created status
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Owner of a workspace can find all the members
    @GetMapping(value = {"/{workspaceId}/members", "/{workspaceId}/members/"})
    public ResponseEntity<SuccessApiResponse<List<WorkspaceMemberDto>>> getAllMembers(@PathVariable UUID workspaceId) {
        List<WorkspaceMemberDto> members = workspaceMemberService.getAllMembers(workspaceId);

        // Set up response
        SuccessApiResponse<List<WorkspaceMemberDto>> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Applied To Be A Member");
        response.setMessage("Successfully applied to be a member");
        response.setData(members);

        // Return the response with 200 Created status
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Verify member
    @PostMapping(value = {"/{workspaceId}/verify-member/{workspaceMemberId}/verify", "/{workspaceId}/verify-member/{workspaceMemberId}/verify/"})
    public ResponseEntity<SuccessApiResponse<WorkspaceMemberDto>> verifyMember(@PathVariable UUID workspaceId, @PathVariable UUID workspaceMemberId) {
        // Verify a Member
        WorkspaceMemberDto verifiedMember = workspaceMemberService.verifyMember(workspaceId, workspaceMemberId, true);

        // Set up response
        SuccessApiResponse<WorkspaceMemberDto> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Verified Successfully");
        response.setMessage("Successfully verified");
        response.setData(verifiedMember);

        // Return the response with 200 Created status
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // Verify member
    @PostMapping(value = {"/{workspaceId}/verify-member/{workspaceMemberId}/reject", "/{workspaceId}/verify-member/{workspaceMemberId}/reject/"})
    public ResponseEntity<SuccessApiResponse<WorkspaceMemberDto>> rejectMember(@PathVariable UUID workspaceId, @PathVariable UUID workspaceMemberId) {
        // Verify a Member
        WorkspaceMemberDto verifiedMember = workspaceMemberService.verifyMember(workspaceId, workspaceMemberId, false);

        // Set up response
        SuccessApiResponse<WorkspaceMemberDto> response = new SuccessApiResponse<>();
        response.setStatus_code(HttpStatus.OK.value());
        response.setStatus_text(HttpStatus.OK.name());
        response.setSuccess(true);
        response.setStatus("Verified Successfully");
        response.setMessage("Successfully verified");
        response.setData(verifiedMember);

        // Return the response with 200 Created status
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
