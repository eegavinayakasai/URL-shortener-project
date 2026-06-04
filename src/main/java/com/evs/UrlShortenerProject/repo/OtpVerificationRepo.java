package com.evs.UrlShortenerProject.repo;

import com.evs.UrlShortenerProject.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OtpVerificationRepo extends JpaRepository<Otp,Long>
{

    @Transactional
    void deleteByEmail(String email);

   Optional<Otp> findByEmail(String email);
}
