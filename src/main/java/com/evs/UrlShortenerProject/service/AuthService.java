package com.evs.UrlShortenerProject.service;

import com.evs.UrlShortenerProject.dto.AuthResponse;
import com.evs.UrlShortenerProject.dto.LoginRequest;
import com.evs.UrlShortenerProject.dto.RegisterRequest;
import com.evs.UrlShortenerProject.exceptionHandler.DuplicateResourceException;
import com.evs.UrlShortenerProject.exceptionHandler.ResourceNotFoundException;
import com.evs.UrlShortenerProject.model.User;
import com.evs.UrlShortenerProject.repo.UserRepo;
import com.evs.UrlShortenerProject.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService
{
    @Value("${api_key}")
    private String API_KEY;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    public AuthResponse login(LoginRequest loginRequest)
    {
        String emailOrUsername = loginRequest.getEmailOrUsername();
        String  password = loginRequest.getPassword();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailOrUsername, password));

        User user = userRepo.findByEmailOrUsername(emailOrUsername, emailOrUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with the credentials entered!"));
            String token = jwtUtil.generateToken(user.getEmail());

            return AuthResponse.builder()
                    .token(token)
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .build();
    }

    public AuthResponse register(RegisterRequest registerRequest)
    {
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        String username = registerRequest.getUsername();

        if(!otpService.isEmailVerified(email))
        {
            throw new IllegalArgumentException("Verify your email by entering otp!");
        }
        if (userRepo.findByUsername(username).isPresent())
        {
            throw new DuplicateResourceException("Username already exists!");
        }
        if(userRepo.existsByEmail(email))
        {
            throw new DuplicateResourceException("Email already exists!");
        }
        User user =  User.builder()
                .email(email)
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();

        User savedUser = userRepo.save(user);
        String token = jwtUtil.generateToken(savedUser.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(savedUser.getEmail())
                .username(savedUser.getUsername())
                .build();
    }
}
