package com.evs.UrlShortenerProject.service;

import com.evs.UrlShortenerProject.exceptionHandler.ExpiryException;
import com.evs.UrlShortenerProject.exceptionHandler.ResourceNotFoundException;
import com.evs.UrlShortenerProject.model.Otp;
import com.evs.UrlShortenerProject.repo.OtpVerificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService
{
    private final OtpVerificationRepo otpVerificationRepo;
    private final EmailService emailService;
    private static final Random RANDOM = new SecureRandom();

    private String generateOtp()
    {
        return String.format("%06d", RANDOM.nextInt(999999));
    }

    public void sendOtp(String email)
    {
        otpVerificationRepo.findByEmail(email).ifPresent(existingOtp -> {
            if(existingOtp.getLastSent() != null &&
                    existingOtp.getLastSent().plusSeconds(60).isAfter(LocalDateTime.now()))
            {
                throw new IllegalArgumentException("Please wait 60 seconds before requesting another OTP");
            }
        });
        String otp = generateOtp();
        Otp otp1 = Otp.builder()
                .email(email)
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(3))
                .lastSent(LocalDateTime.now())
                .build();
        otpVerificationRepo.deleteByEmail(email);
        otpVerificationRepo.save(otp1);
        emailService.sendOtpToEmail(email,otp);

    }

    public void verifyOtp(String email,String otp)
    {
        Otp otp1 = otpVerificationRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("No OTP found, request a new OTP!"));
        if(otp1.getFailedAttempts() >= 3 )
        {
            otpVerificationRepo.delete(otp1);
            throw new IllegalArgumentException("Too many failed attempts, request a new OTP");
        }
        String savedOtp =  otp1.getOtp();
        if(otp1.getExpiresAt().isBefore(LocalDateTime.now()))
        {
            throw new ExpiryException("OTP expired!");
        }
        if(!savedOtp.equals(otp))
        {
            otp1.setFailedAttempts(otp1.getFailedAttempts() + 1);
            otpVerificationRepo.save(otp1);
            throw new IllegalArgumentException("Wrong OTP");
        }
        otp1.setVerified(true);
        otpVerificationRepo.save(otp1);
    }
    public boolean isEmailVerified(String email)
    {
        Otp otp = otpVerificationRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found, request an OTP!"));
        return otp.getVerified();
    }
}
