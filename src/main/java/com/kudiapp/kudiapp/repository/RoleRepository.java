package com.kudiapp.kudiapp.repository;

import com.kudiapp.kudiapp.models.approles.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByRoleName(String roleAdmin);

    Optional<Role> findByRoleName(String roleUser);
}
