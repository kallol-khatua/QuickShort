package com.quickshort.auth.repository;

import com.quickshort.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    //    Optional<User> findByEmail(String email);
    User findByEmail(String email);
}
