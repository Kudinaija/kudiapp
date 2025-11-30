package com.kudiapp.kudiapp.repository;

import com.kudiapp.kudiapp.models.NewsLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsLetterRepository extends JpaRepository<NewsLetter,Long> {
    boolean existsByEmail(String email);
}
