package com.quickshort.workspace.repository;

import com.quickshort.workspace.models.ShortUrl;
import com.quickshort.workspace.models.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, UUID> {
    List<ShortUrl> findByWorkspaceId(Workspace workspaceId);
}
