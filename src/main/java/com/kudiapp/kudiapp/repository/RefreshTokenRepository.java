package com.kudiapp.kudiapp.repository;


import com.kudiapp.kudiapp.models.RefreshToken;
import com.kudiapp.kudiapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);

    Optional<RefreshToken> findByUserId(Long userId);
}
