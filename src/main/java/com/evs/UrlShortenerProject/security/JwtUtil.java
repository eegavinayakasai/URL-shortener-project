package com.evs.UrlShortenerProject.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil
{
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiry}")
    private Long EXPIRATION_TIME;

    private SecretKey secretKey;

    @PostConstruct
    public void init()
    {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }
    public String generateToken(String email)
    {

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey,Jwts.SIG.HS256)
                .compact();
    }
    public String extractEmail(String token)
    {
        return extractAllClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token)
    {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String email)
    {
        String extractedEmail =  extractEmail(token);
        return email.equals(extractedEmail) && !isTokenExpired(token);
    }
    public Claims extractAllClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
