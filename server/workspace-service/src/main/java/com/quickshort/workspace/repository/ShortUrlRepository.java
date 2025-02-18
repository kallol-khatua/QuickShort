package com.quickshort.workspace.repository;

import com.quickshort.workspace.models.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, UUID> {
}
