package com.quickshort.workspace.repository;


import com.quickshort.workspace.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    //    Optional<User> findByEmail(String email);
    User findByEmail(String email);
}
