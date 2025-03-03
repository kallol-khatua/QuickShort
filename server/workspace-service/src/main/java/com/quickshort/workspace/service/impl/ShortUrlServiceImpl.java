package com.quickshort.workspace.service.impl;

import com.quickshort.common.enums.MemberStatus;
import com.quickshort.common.enums.ShortUrlStatus;
import com.quickshort.common.events.ShortUrlCreationEvent;
import com.quickshort.common.exception.*;
import com.quickshort.common.payload.ShortUrlPayload;
import com.quickshort.workspace.dto.ShortUrlDto;
import com.quickshort.workspace.kafka.producers.URLCreationProducer;
import com.quickshort.workspace.mapper.ShortUrlMapper;
import com.quickshort.workspace.models.ShortUrl;
import com.quickshort.workspace.models.User;
import com.quickshort.workspace.models.Workspace;
import com.quickshort.workspace.models.WorkspaceMember;
import com.quickshort.workspace.repository.ShortUrlRepository;
import com.quickshort.workspace.repository.UserRepository;
import com.quickshort.workspace.repository.WorkspaceMemberRepository;
import com.quickshort.workspace.repository.WorkspaceRepository;
import com.quickshort.workspace.service.ShortUrlService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShortUrlServiceImpl.class);

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Autowired
    private URLCreationProducer urlCreationProducer;

    private User getUserFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }

    // Create short url, member or an owner can create short url, and they must be verified
    @Transactional
    @Override
    public ShortUrlDto generateShortUrl(UUID workspaceId, ShortUrlDto shortUrlDto) {
        try {
            // Find the workspace
            Optional<Workspace> existingWorkspace = workspaceRepository.findById(workspaceId);


            // If workspace is not present with the id, then throw error
            if (existingWorkspace.isEmpty()) {
                List<FieldError> errors = new ArrayList<>();
                errors.add(new FieldError("No workspace found", "workspace_id"));
                throw new BadRequestException("Invalid Data Provided", "No workspace found for the id", errors);
            }


            // Check data validation, original url must be provided
            List<FieldError> errors = new ArrayList<>();
            if (shortUrlDto.getOriginalUrl() == null || shortUrlDto.getOriginalUrl().isEmpty()) {
                errors.add(new FieldError("Original URL is Required", "originalUrl"));
            }

            if (!errors.isEmpty()) {
                throw new BadRequestException("Invalid Data Provided", "Please fill all the details", errors);
            }


            // Check user is a member of the workspace or not
            User currUser = getUserFromAuthentication();
            Workspace workspace = existingWorkspace.get();
            Optional<WorkspaceMember> workspaceMember = workspaceMemberRepository.findByUserIdAndWorkspaceId(currUser, workspace);
            // If the current user not associated with the workspace, then throw error
            if (workspaceMember.isEmpty()) {
                errors.add(new FieldError("User not associated with the workspace"));
                throw new ForbiddenException("User Do Not Have Access To The workspace", "User not associated with the workspace", errors);
            }


            // Check user is verified or not
            WorkspaceMember existingMember = workspaceMember.get();
            // If user is not verified, then throw error
            if (existingMember.getStatus() != MemberStatus.VERIFIED) {
                errors.add(new FieldError("User not is not a VERIFIED OWNER or MEMBER of the Workspace"));
                throw new ForbiddenException("User Not Verified", "User not is not a VERIFIED OWNER or MEMBER of the Workspace", errors);
            }


            // Check link creation limit, if exceed then do not allow to create link
            if (workspace.getCreatedLinksThisMonth() >= workspace.getLinkCreationLimitPerMonth()) {
                errors.add(new FieldError("Can not create new link this month"));
                throw new TooManyRequest("Link Creation Limit Exceed", "Limit Exceed: Can not create new link this month", errors);
            }


            // Create Short URL
            ShortUrl shortUrl = ShortUrlMapper.mapToShortURL(shortUrlDto);
            shortUrl.setWorkspaceId(workspace);
            shortUrl.setWorkspaceMemberID(existingMember);
            shortUrl.setOriginalUrl(shortUrlDto.getOriginalUrl());
            // generate short code
            shortUrl.setShortCode(generateShortCode());
            shortUrl.setExpiresAt(shortUrlDto.getExpiresAt());
            shortUrl.setStatus(ShortUrlStatus.ACTIVE);
            shortUrl.setActive(true);

            ShortUrl savedUrl = shortUrlRepository.save(shortUrl);


            // Update link creation limit
            workspace.setCreatedLinksThisMonth(workspace.getCreatedLinksThisMonth() + 1);
            workspaceRepository.save(workspace);


            // Send created link to kafka
            ShortUrlCreationEvent event = new ShortUrlCreationEvent();
            event.setStatus("Short URL Created");
            event.setMessage("New short url created");
            event.setKey(savedUrl.getId().toString());
            // Set payload
            ShortUrlPayload payload = getPayload(savedUrl);
            event.setShortUrlPayload(payload);
            urlCreationProducer.sendURLCreationMessage(event.getKey(), event);


            return ShortUrlMapper.mapToShortURLDto(savedUrl);
        } catch (BadRequestException | ForbiddenException | TooManyRequest exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during short url creation", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }


    private ShortUrlPayload getPayload(ShortUrl url) {
        ShortUrlPayload payload = new ShortUrlPayload();

        payload.setId(url.getId());
        payload.setWorkspaceId(url.getWorkspaceId().getId());
        payload.setWorkspaceMemberID(url.getWorkspaceMemberID().getId());
        payload.setOriginalUrl(url.getOriginalUrl());
        payload.setShortCode(url.getShortCode());
        payload.setExpiresAt(url.getExpiresAt());
        payload.setActive(url.isActive());
        payload.setStatus(url.getStatus());
        payload.setCreatedAt(url.getCreatedAt());
        payload.setUpdatedAt(url.getUpdatedAt());

        return payload;
    }


    private String generateShortCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123467890abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder shortCode = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            shortCode.append(chars.charAt(random.nextInt(chars.length())));
        }
        return shortCode.toString();
    }

    // Find all the links under a workspace, user must have to be a member or an owner, and status = verified
    @Override
    public Page<ShortUrl> getAllUrl(UUID workspaceId, int page, int size) {
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


            // Check user is a member of the workspace or not
            User currUser = getUserFromAuthentication();
            Workspace workspace = existingWorkspace.get();
            Optional<WorkspaceMember> workspaceMember = getWorkspaceMembers(currUser, workspace);
            // If the current user not associated with the workspace, then throw error
            if (workspaceMember.isEmpty()) {
                errors.add(new FieldError("User not associated with the workspace"));
                throw new ForbiddenException("User Do Not Have Access To The workspace", "User not associated with the workspace", errors);
            }


            // Check user is verified or not
            WorkspaceMember existingMember = workspaceMember.get();
            // If user is not verified, then throw error
            if (existingMember.getStatus() != MemberStatus.VERIFIED) {
                errors.add(new FieldError("User not is not a VERIFIED OWNER or MEMBER of the Workspace"));
                throw new ForbiddenException("User Not Verified", "User not is not a VERIFIED OWNER or MEMBER of the Workspace", errors);
            }

//            List<ShortUrl> shortUrlList = shortUrlRepository.findByWorkspaceId(workspace);
//
//            List<ShortUrlDto> shortUrlDtoList = shortUrlList.stream()
//                    .map(shortUrl -> new ShortUrlDto(
//                            shortUrl.getId(),
//                            shortUrl.getWorkspaceId().getId(),
//                            shortUrl.getWorkspaceMemberID().getId(),
//                            shortUrl.getOriginalUrl(),
//                            shortUrl.getShortCode(),
//                            shortUrl.getExpiresAt(),
//                            shortUrl.isActive(),
//                            shortUrl.getStatus()
//                    ))
//                    .collect(Collectors.toList());
//
//            LOGGER.info("All the short url found for workspace -> {}", workspace.getId());

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            return shortUrlRepository.findByWorkspaceId(pageable, workspace);
        } catch (BadRequestException | ForbiddenException exception) {
            throw exception;
        } catch (Exception e) {
            LOGGER.error("Unexpected error during while finding short url under a workspace", e);
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("Internal Server Error"));
            throw new InternalServerErrorException("Internal Server Error", "Internal Server Error", errors);
        }
    }

    private Optional<WorkspaceMember> getWorkspaceMembers(User currUser, Workspace workspace) {
        return workspaceMemberRepository.findByUserIdAndWorkspaceId(currUser, workspace);
    }
}
