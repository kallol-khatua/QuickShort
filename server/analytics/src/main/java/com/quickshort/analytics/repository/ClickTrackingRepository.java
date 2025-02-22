package com.quickshort.analytics.repository;

import com.quickshort.analytics.model.ClickTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClickTrackingRepository extends JpaRepository<ClickTracking, UUID> {
}
