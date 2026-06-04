package com.evs.UrlShortenerProject.controller;

import com.evs.UrlShortenerProject.dto.*;
import com.evs.UrlShortenerProject.service.AuthService;
import com.evs.UrlShortenerProject.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest)
    {
        AuthResponse authResponse = authService.login(loginRequest);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest)
    {
        AuthResponse authResponse = authService.register(registerRequest);

        return new  ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/request-otp")
    public ResponseEntity<String>  requestOtp(@Valid @RequestBody OtpRequest otpRequest)
    {
        otpService.sendOtp(otpRequest.getEmail());
        return ResponseEntity.ok("OTP sent successfully to " +  otpRequest.getEmail());
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody OtpVerifyRequest otpVerifyRequest)
    {
        otpService.verifyOtp(otpVerifyRequest.getEmail(), otpVerifyRequest.getOtp());
        return ResponseEntity.ok("Email verified successfully");
    }
}
