package com.quickshort.workspace.service.impl;

import com.quickshort.common.enums.MemberStatus;
import com.quickshort.common.enums.MemberType;
import com.quickshort.common.exception.BadRequestException;
import com.quickshort.common.exception.FieldError;
import com.quickshort.common.exception.ForbiddenException;
import com.quickshort.common.exception.InternalServerErrorException;
import com.quickshort.workspace.dto.WorkspaceDto;
import com.quickshort.workspace.dto.WorkspaceMemberDto;
import com.quickshort.workspace.mapper.WorkspaceMemberMapper;
import com.quickshort.workspace.models.User;
import com.quickshort.workspace.models.Workspace;
import com.quickshort.workspace.models.WorkspaceMember;
import com.quickshort.workspace.repository.UserRepository;
import com.quickshort.workspace.repository.WorkspaceMemberRepository;
import com.quickshort.workspace.repository.WorkspaceRepository;
import com.quickshort.workspace.service.WorkspaceMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkspaceMemberServiceImpl implements WorkspaceMemberService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private RedisWorkspaceMemberService redisWorkspaceMemberService;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceMemberServiceImpl.class);

    private User getUserFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }

    // Find all the workspace where user is either owner or a member, and user is verified
    @Override
    public List<WorkspaceMemberDto> allWorkspaceWhereUserIsMemberOrOwner() {
        try {
            User currUser = getUserFromAuthentication();

            // find for existing cache - if found return that
            List<WorkspaceMemberDto> cacheList = redisWorkspaceMemberService.findInCache(currUser);

            if (cacheList != null) {
                LOGGER.info("Found record in cache for finding workspace member list where user is owner or member, userId -> {}", currUser.getId().toString());
                return cacheList;
            }


            // Find all the workspace member list, with status = verified
            List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findAllByUserIdAndStatus(currUser, MemberStatus.VERIFIED);

            List<WorkspaceMemberDto> workspaceMemberDtoList = workspaceMembers.stream()
                    .map(workspaceMember -> new WorkspaceMemberDto(
                            workspaceMember.getId(),
                            workspaceMember.getWorkspaceId(),
                            workspaceMember.getUserId(),
                            workspaceMember.getMemberType(),
                            workspaceMember.getStatus()
                    ))
                    .collect(Collectors.toList());

            // Cache to redis
            redisWorkspaceMemberService.addToCache(currUser, workspaceMemberDtoList);
            LOGGER.info("Record cached for finding workspace member list where user is owner or member, userId -> {}", currUser.getId().toString());

            // return the resulting list
            return workspaceMemberDtoList;
        } catch (Exception e) {
            LOGGER.error("Unexpected error finding workspace member list where user is owner or member ", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }

    // Find all the workspace where user is owner
    @Override
    public List<WorkspaceMemberDto> allWorkspaceWhereUserIsOwner() {
        User currUser = getUserFromAuthentication();

        List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findAllByUserIdAndMemberType(currUser, MemberType.OWNER);

        return workspaceMembers.stream()
                .map(workspaceMember -> new WorkspaceMemberDto(
                        workspaceMember.getId(),
                        workspaceMember.getWorkspaceId(),
                        workspaceMember.getUserId(),
                        workspaceMember.getMemberType(),
                        workspaceMember.getStatus()
                ))
                .collect(Collectors.toList());
    }


    // Find all the workspace where user is owner
    @Override
    public List<WorkspaceMemberDto> allWorkspaceWhereUserIsMember() {
        User currUser = getUserFromAuthentication();

        List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findAllByUserIdAndMemberType(currUser, MemberType.MEMBER);

        return workspaceMembers.stream()
                .map(workspaceMember -> new WorkspaceMemberDto(
                        workspaceMember.getId(),
                        workspaceMember.getWorkspaceId(),
                        workspaceMember.getUserId(),
                        workspaceMember.getMemberType(),
                        workspaceMember.getStatus()
                ))
                .collect(Collectors.toList());
    }


    // Apply to be a member of a workspace
    @Transactional
    @Override
    public WorkspaceMemberDto joinAsMember(UUID workspaceId) {
        try {
            // Find the workspace
            Optional<Workspace> existingWorkspace = workspaceRepository.findById(workspaceId);


            // If workspace is not present with the id, then throw error
            if (existingWorkspace.isEmpty()) {
                List<FieldError> errors = new ArrayList<>();
                errors.add(new FieldError("No workspace found", "workspace_id"));
                throw new BadRequestException("Invalid Data Provided", "No workspace found for the id", errors);
            }


            List<FieldError> errors = new ArrayList<>();


            // Check user is already a member of the workspace or not
            User currUser = getUserFromAuthentication();
            Workspace workspace = existingWorkspace.get();
            Optional<WorkspaceMember> workspaceMember = workspaceMemberRepository.findByUserIdAndWorkspaceId(currUser, workspace);
            // If the current user already associated with the workspace, then throw error
            if (workspaceMember.isPresent()) {
                errors.add(new FieldError("User already associated with the workspace"));
                throw new BadRequestException("User Already Associated With The workspace", "User already associated with the workspace", errors);
            }


            // Check member count limit exceed or not, if exceed then throw error
            if (workspace.getMemberCount() == workspace.getMemberLimit()) {
                errors.add(new FieldError("Can not be a member of the workspace"));
                throw new BadRequestException("Workspace Member Limit Exceed", "Can not be a member of the workspace", errors);
            }


            // Create workspace member
            WorkspaceMember newWorkspaceMember = new WorkspaceMember();
            newWorkspaceMember.setWorkspaceId(workspace);
            newWorkspaceMember.setUserId(currUser);
            newWorkspaceMember.setMemberType(MemberType.MEMBER);
            newWorkspaceMember.setStatus(MemberStatus.APPLIED);
            WorkspaceMember savedWorkspaceMember = workspaceMemberRepository.save(newWorkspaceMember);


            return WorkspaceMemberMapper.mapToWorkspaceMemberDto(savedWorkspaceMember);
        } catch (BadRequestException | ForbiddenException exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during while finding short url under a workspace", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }


    // Get all the members under a workspace, only a verified owner of the workspace cah fetch the data
    @Override
    public List<WorkspaceMemberDto> getAllMembers(UUID workspaceId) {
        try {
            // Find the workspace
            Optional<Workspace> existingWorkspace = workspaceRepository.findById(workspaceId);


            // If workspace is not present with the id, then throw error
            if (existingWorkspace.isEmpty()) {
                List<FieldError> errors = new ArrayList<>();
                errors.add(new FieldError("No workspace found", "workspace_id"));
                throw new BadRequestException("Invalid Data Provided", "No workspace found for the id", errors);
            }


            List<FieldError> errors = new ArrayList<>();

            // Check user associated with the workspace or not
            User currUser = getUserFromAuthentication();
            Workspace workspace = existingWorkspace.get();
            Optional<WorkspaceMember> member = workspaceMemberRepository.findByUserIdAndWorkspaceId(currUser, workspace);
            // If the current user not associated with the workspace, then throw error
            if (member.isEmpty()) {
                errors.add(new FieldError("User not associated with the workspace"));
                throw new ForbiddenException("Invalid Data Provided", "User not associated with the workspace", errors);
            }


            // If the current user associated with the workspace but not an OWNER, then do not allow to update workspace details
            if (member.get().getMemberType() != MemberType.OWNER) {
                errors.add(new FieldError("User is not an OWNER of the Workspace"));
                throw new ForbiddenException("Invalid Data Provided", "User is not an OWNER of the Workspace", errors);
            }


            // If OWNER is not verified, then not allow to update
            if (member.get().getStatus() != MemberStatus.VERIFIED) {
                errors.add(new FieldError("User is not a VERIFIED OWNER of the Workspace"));
                throw new ForbiddenException("Invalid Data Provided", "User is not a VERIFIED OWNER of the Workspace", errors);
            }


            List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findByWorkspaceId(workspace);

            return workspaceMembers.stream()
                    .map(workspaceMember -> new WorkspaceMemberDto(
                            workspaceMember.getId(),
                            workspaceMember.getWorkspaceId(),
                            workspaceMember.getUserId(),
                            workspaceMember.getMemberType(),
                            workspaceMember.getStatus()
                    ))
                    .collect(Collectors.toList());

        } catch (BadRequestException | ForbiddenException exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during while finding short url under a workspace", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }


    // Verify member, only a verified owner of the workspace cah fetch the data
    @Transactional
    @Override
    public WorkspaceMemberDto verifyMember(UUID workspaceId, UUID workspaceMemberId, boolean isVerified) {
        try {
            // Find the workspace
            Optional<Workspace> existingWorkspace = workspaceRepository.findById(workspaceId);


            // If workspace is not present with the id, then throw error
            if (existingWorkspace.isEmpty()) {
                List<FieldError> errors = new ArrayList<>();
                errors.add(new FieldError("No workspace found", "workspace_id"));
                throw new BadRequestException("Invalid Data Provided", "No workspace found for the id", errors);
            }


            List<FieldError> errors = new ArrayList<>();

            // Check user associated with the workspace or not
            User currUser = getUserFromAuthentication();
            Workspace workspace = existingWorkspace.get();
            Optional<WorkspaceMember> member = workspaceMemberRepository.findByUserIdAndWorkspaceId(currUser, workspace);
            // If the current user not associated with the workspace, then throw error
            if (member.isEmpty()) {
                errors.add(new FieldError("User not associated with the workspace"));
                throw new ForbiddenException("Invalid Data Provided", "User not associated with the workspace", errors);
            }


            // If the current user associated with the workspace but not an OWNER, then do not allow to update workspace details
            if (member.get().getMemberType() != MemberType.OWNER) {
                errors.add(new FieldError("User is not an OWNER of the Workspace"));
                throw new ForbiddenException("Invalid Data Provided", "User is not an OWNER of the Workspace", errors);
            }


            // If OWNER is not verified, then not allow to update
            if (member.get().getStatus() != MemberStatus.VERIFIED) {
                errors.add(new FieldError("User is not a VERIFIED OWNER of the Workspace"));
                throw new ForbiddenException("Invalid Data Provided", "User is not a VERIFIED OWNER of the Workspace", errors);
            }


            // Member id should exist in the workspace
            Optional<WorkspaceMember> workspaceMember = workspaceMemberRepository.findByIdAndWorkspaceId(workspaceMemberId, workspace);
            if (workspaceMember.isEmpty()) {
                errors.add(new FieldError("No member found", "workspace_member_id"));
                throw new BadRequestException("Invalid Data Provided", "No member found for the id", errors);
            }
            WorkspaceMember appliedMember = workspaceMember.get();


            // If already verified or rejected then not allow to again verify
            if (appliedMember.getStatus() != MemberStatus.APPLIED) {
                errors.add(new FieldError("Already verified", "workspace_member_id"));
                throw new BadRequestException("Invalid Data Provided", "Already verified", errors);
            }


            // In the workspace count should less than the limit to add a member
            if (workspace.getMemberCount() >= workspace.getMemberLimit()) {
                errors.add(new FieldError("Can not be verified as member of the workspace"));
                throw new BadRequestException("Workspace Member Limit Exceed", "Can not be verified as member of the workspace", errors);
            }


            // Update status to verify or reject
            if (isVerified) {
                appliedMember.setStatus(MemberStatus.VERIFIED);
            } else {
                appliedMember.setStatus(MemberStatus.REJECTED);
            }
            WorkspaceMember updatedMember = workspaceMemberRepository.save(appliedMember);


            // Update count if verified
            if (isVerified) {
                workspace.setMemberCount(workspace.getMemberCount() + 1);
                workspaceRepository.save(workspace);
            }


            // TODO: send email to the verified member


            return WorkspaceMemberMapper.mapToWorkspaceMemberDto(updatedMember);
        } catch (BadRequestException | ForbiddenException exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during while finding short url under a workspace", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }
}
