package com.kudiapp.kudiapp.config.dataseeder;


import com.kudiapp.kudiapp.models.approles.Role;
import com.kudiapp.kudiapp.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RoleDataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(RoleDataSeeder.class);
    private final RoleRepository roleRepository;

    public RoleDataSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @EventListener
    @Transactional
    public void seedInitialRoles(ContextRefreshedEvent event) {
        List<String> defaultRoles = List.of("ROLE_USER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN");

        for (String roleName : defaultRoles) {
            if (!roleRepository.existsByRoleName(roleName)) {
                Role role = Role.builder()
                        .roleName(roleName)
                        .createdAt(LocalDateTime.now())
                        .build();
                roleRepository.save(role);
            }
        }
    }
}