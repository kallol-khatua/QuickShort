package com.quickshort.workspace.service.impl;

import com.quickshort.common.enums.MemberStatus;
import com.quickshort.common.enums.MemberType;
import com.quickshort.common.enums.WorkspaceStatus;
import com.quickshort.common.enums.WorkspaceType;
import com.quickshort.common.events.WorkspaceCreationEvent;
import com.quickshort.common.exception.BadRequestException;
import com.quickshort.common.exception.FieldError;
import com.quickshort.common.exception.InternalServerErrorException;
import com.quickshort.common.payload.ShortUrlPayload;
import com.quickshort.common.payload.WorkspacePayload;
import com.quickshort.workspace.dto.WorkspaceDto;
import com.quickshort.workspace.kafka.producers.WorkspaceCreationProducer;
import com.quickshort.workspace.mapper.WorkspaceMapper;
import com.quickshort.workspace.models.User;
import com.quickshort.workspace.models.Workspace;
import com.quickshort.workspace.models.WorkspaceMember;
import com.quickshort.workspace.repository.UserRepository;
import com.quickshort.workspace.repository.WorkspaceMemberRepository;
import com.quickshort.workspace.repository.WorkspaceRepository;
import com.quickshort.workspace.service.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private WorkspaceCreationProducer workspaceCreationProducer;

    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Value("${spring.workspace.free.link-creation-limit}")
    private int freeWorkspaceLinkCreationLimit;

    @Value("${spring.workspace.free.member-limit}")
    private int freeWorkspaceMemberLimit;

    @Value("${spring.workspace.pro.link-creation-limit}")
    private int proWorkspaceLinkCreationLimit;

    @Value("${spring.workspace.pro.member-limit}")
    private int proWorkspaceMemberLimit;

    @Value("${spring.workspace.business.link-creation-limit}")
    private int businessWorkspaceLinkCreationLimit;

    @Value("${spring.workspace.business.member-limit}")
    private int businessWorkspaceMemberLimit;

    private User getUserFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }

    // Create a new workspace (type = free, monthly limit = 25)
    @Transactional
    @Override
    public WorkspaceDto createWorkspace(WorkspaceDto workspaceDto) {
        try {

            List<FieldError> errors = new ArrayList<>();
            if (workspaceDto.getName() == null || workspaceDto.getName().isEmpty()) {
                errors.add(new FieldError("Workspace name is required", "name"));
            }
            if (!errors.isEmpty()) {
                throw new BadRequestException("Invalid Data Provided", "Please fill all the details", errors);
            }

            User currUser = getUserFromAuthentication();


            // Create new free workspace
            Workspace newWorkspace = WorkspaceMapper.mapToWorkspace(workspaceDto);
            newWorkspace.setCreatedBy(currUser);
            newWorkspace.setType(WorkspaceType.FREE);
            newWorkspace.setLinkCreationLimitPerMonth(freeWorkspaceLinkCreationLimit);
            newWorkspace.setCreatedLinksThisMonth(0);
            newWorkspace.setMemberLimit(freeWorkspaceMemberLimit);
            newWorkspace.setMemberCount(1);

            LocalDate currentDate = LocalDate.now();
            newWorkspace.setLastResetDate(currentDate);

            LocalDate nextMonthDate = currentDate.plusMonths(1);
            newWorkspace.setNextResetDate(nextMonthDate);

            newWorkspace.setWorkspaceStatus(WorkspaceStatus.ACTIVE);

            Workspace savedWorkspace = workspaceRepository.save(newWorkspace);


            // Send created workspace to kafka
            WorkspaceCreationEvent event = new WorkspaceCreationEvent();
            event.setKey(savedWorkspace.getId().toString());
            event.setMessage("New free workspace created");
            event.setStatus("Workspace Created");
            // Set payload
            WorkspacePayload payload = getWorkspacePayload(savedWorkspace);
            event.setWorkspacePayload(payload);

            workspaceCreationProducer.sendWorkspaceCreationMessage(event.getKey(), event);


            // Create workspace member
            WorkspaceMember newWorkspaceMember = new WorkspaceMember();
            newWorkspaceMember.setWorkspaceId(savedWorkspace);
            newWorkspaceMember.setUserId(currUser);
            newWorkspaceMember.setMemberType(MemberType.OWNER);
            newWorkspaceMember.setStatus(MemberStatus.VERIFIED);
            WorkspaceMember savedWorkspaceMember = workspaceMemberRepository.save(newWorkspaceMember);


            return WorkspaceMapper.mapToWorkspaceDto(savedWorkspace);
        } catch (BadRequestException exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during workspace creation", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }

    // Function to get workspace payload from saved workspace
    private WorkspacePayload getWorkspacePayload(Workspace savedWorkspace) {
        WorkspacePayload payload = new WorkspacePayload();

        payload.setId(savedWorkspace.getId());
        payload.setType(savedWorkspace.getType());
        payload.setCreatedAt(savedWorkspace.getCreatedAt());
        payload.setUpdatedAt(savedWorkspace.getUpdatedAt());

        payload.setLinkCreationLimitPerMonth(savedWorkspace.getLinkCreationLimitPerMonth());
        payload.setMemberLimit(savedWorkspace.getMemberLimit());

        payload.setLastResetDate(savedWorkspace.getLastResetDate());
        payload.setNextResetDate(savedWorkspace.getNextResetDate());
        payload.setNextBillingDate(savedWorkspace.getNextBillingDate());

        payload.setWorkspaceStatus(savedWorkspace.getWorkspaceStatus());

        return payload;
    }

    // Update existing workspace details
    @Transactional
    @Override
    public WorkspaceDto updateWorkspace(UUID workspaceId, WorkspaceDto workspaceDto) {
        try {
            Optional<Workspace> existingWorkspace = workspaceRepository.findById(workspaceId);


            // If workspace is not present with the id, then throw error
            if (existingWorkspace.isEmpty()) {
                List<FieldError> errors = new ArrayList<>();
                errors.add(new FieldError("No workspace found", "workspace_id"));
                throw new BadRequestException("Invalid Data Provided", "No workspace found for the id", errors);
            }


            // Check data validation
            List<FieldError> errors = new ArrayList<>();
            if (workspaceDto.getName() == null || workspaceDto.getName().isEmpty()) {
                errors.add(new FieldError("Workspace name is required", "name"));
            }
            if (workspaceDto.getType() == null || workspaceDto.getType().name().isEmpty()) {
                errors.add(new FieldError("Workspace type is required", "type"));
            }
            if (!errors.isEmpty()) {
                throw new BadRequestException("Invalid Data Provided", "Please fill all the details", errors);
            }


            User currUser = getUserFromAuthentication();
            Workspace workspace = existingWorkspace.get();
            Optional<WorkspaceMember> member = workspaceMemberRepository.findByUserIdAndWorkspaceId(currUser, workspace);
            // If the current user not associated with the workspace, then throw error
            if (member.isEmpty()) {
                errors.add(new FieldError("User not associated with the workspace"));
                throw new BadRequestException("Invalid Data Provided", "User not associated with the workspace", errors);
            }


            // If the current user associated with the workspace but not an OWNER, then do not allow to update workspace details
            if (member.get().getMemberType() != MemberType.OWNER) {
                errors.add(new FieldError("User not is not an OWNER of the Workspace"));
                throw new BadRequestException("Invalid Data Provided", "User not is not an OWNER of the Workspace", errors);
            }


            // If OWNER is not verified, then not allow to update
            if (member.get().getStatus() != MemberStatus.VERIFIED) {
                errors.add(new FieldError("User not is not a VERIFIED OWNER of the Workspace"));
                throw new BadRequestException("Invalid Data Provided", "User not is not a VERIFIED OWNER of the Workspace", errors);
            }


            // Update the existing workspace
            workspace.setName(workspaceDto.getName());
            workspace.setType(workspaceDto.getType());
            if (workspaceDto.getType() == WorkspaceType.BUSINESS) {
                workspace.setLinkCreationLimitPerMonth(businessWorkspaceLinkCreationLimit);
                workspace.setMemberLimit(businessWorkspaceMemberLimit);

            } else if (workspaceDto.getType() == WorkspaceType.PRO) {
                workspace.setLinkCreationLimitPerMonth(proWorkspaceLinkCreationLimit);
                workspace.setMemberLimit(proWorkspaceMemberLimit);
            } else {
                workspace.setLinkCreationLimitPerMonth(freeWorkspaceLinkCreationLimit);
                workspace.setMemberLimit(freeWorkspaceMemberLimit);
            }
            Workspace updatedWorkspace = workspaceRepository.save(workspace);


            return WorkspaceMapper.mapToWorkspaceDto(updatedWorkspace);
        } catch (BadRequestException exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during workspace details updation", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }


    // Scheduled function to reset monthly link creation count
    @Scheduled(cron = "0 0 0 1 * ?") // Runs at midnight on the 1st of every month
    public void resetMonthlyUrlCount() {
        try {
            List<Workspace> workspaces = workspaceRepository.findAll();

            for (Workspace workspace : workspaces) {
                workspace.setCreatedLinksThisMonth(0);
            }

            workspaceRepository.saveAll(workspaces);
            LOGGER.info("Monthly URL count reset to 0 for all workspaces.");

        } catch (Exception e) {
            LOGGER.error("Error while resetting monthly url creation count");
        }
    }


}
