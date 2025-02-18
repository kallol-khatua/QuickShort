package com.quickshort.workspace.service.impl;

import com.quickshort.common.enums.MemberStatus;
import com.quickshort.common.enums.MemberType;
import com.quickshort.common.exception.FieldError;
import com.quickshort.common.exception.InternalServerErrorException;
import com.quickshort.workspace.dto.WorkspaceMemberDto;
import com.quickshort.workspace.models.User;
import com.quickshort.workspace.models.WorkspaceMember;
import com.quickshort.workspace.repository.UserRepository;
import com.quickshort.workspace.repository.WorkspaceMemberRepository;
import com.quickshort.workspace.service.WorkspaceMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkspaceMemberServiceImpl implements WorkspaceMemberService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;

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
}
