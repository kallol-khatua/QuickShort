package com.quickshort.workspace.repository;

import com.quickshort.common.enums.MemberStatus;
import com.quickshort.common.enums.MemberType;
import com.quickshort.workspace.models.User;
import com.quickshort.workspace.models.Workspace;
import com.quickshort.workspace.models.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, UUID> {
    List<WorkspaceMember> findAllByUserId(User user);

    List<WorkspaceMember> findAllByUserIdAndStatus(User user, MemberStatus status);

    List<WorkspaceMember> findAllByUserIdAndMemberType(User user, MemberType memberType);

    Optional<WorkspaceMember> findByUserIdAndWorkspaceId(User user, Workspace workspace);

    List<WorkspaceMember> findByWorkspaceId(Workspace workspace);
}
