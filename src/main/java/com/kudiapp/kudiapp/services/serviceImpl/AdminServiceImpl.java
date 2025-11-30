package com.kudiapp.kudiapp.services.serviceImpl;

import com.kudiapp.kudiapp.repository.RoleRepository;
import com.kudiapp.kudiapp.repository.UserRepository;
import com.kudiapp.kudiapp.services.AdminService;
import com.kudiapp.kudiapp.services.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;


    public AdminServiceImpl(RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserRepository userRepository, EmailService emailService) {
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
}
