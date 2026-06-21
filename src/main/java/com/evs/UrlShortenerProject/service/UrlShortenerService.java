    package com.evs.UrlShortenerProject.service;

    import com.evs.UrlShortenerProject.dto.ShortenRequest;
    import com.evs.UrlShortenerProject.dto.ShortenResponse;
    import com.evs.UrlShortenerProject.exceptionHandler.DuplicateResourceException;
    import com.evs.UrlShortenerProject.exceptionHandler.ExpiryException;
    import com.evs.UrlShortenerProject.exceptionHandler.ResourceNotFoundException;
    import com.evs.UrlShortenerProject.model.UrlMapping;
    import com.evs.UrlShortenerProject.model.User;
    import com.evs.UrlShortenerProject.repo.UrlMappingRepo;
    import com.evs.UrlShortenerProject.repo.UserRepo;
    import com.google.zxing.BarcodeFormat;
    import com.google.zxing.WriterException;
    import com.google.zxing.client.j2se.MatrixToImageWriter;
    import com.google.zxing.common.BitMatrix;
    import com.google.zxing.qrcode.QRCodeWriter;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.cache.annotation.CacheEvict;
    import org.springframework.cache.annotation.Cacheable;
    import org.springframework.context.annotation.Bean;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.data.redis.core.StringRedisTemplate;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Service;

    import java.io.ByteArrayOutputStream;
    import java.io.IOException;
    import java.security.SecureRandom;
    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Random;

    @Service
    @RequiredArgsConstructor
    public class UrlShortenerService
    {
        private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        private static final int CODE_LENGTH = 6;
        private static final Random RANDOM = new SecureRandom();
        private  final UrlMappingRepo urlMappingRepo;
        private final UserRepo userRepo;
        private final StringRedisTemplate redisTemplate ;

        @Value("${app.base-url}")
        private String baseUrl;


        public ShortenResponse getStats(String shortCode)
        {
            UrlMapping urlMapping = urlMappingRepo.findByShortCode(shortCode).orElseThrow(() -> new ResourceNotFoundException("Url not found check again!"));
            String key = "clickCount::" + shortCode;
            System.out.println("db stats");
            Object value =  redisTemplate.opsForValue().get(key);
            System.out.println("db stats2");
            Long count;
            if(value == null)
            {
                count = 0L;
            }
            else
            {
                count = Long.parseLong(value.toString());
            }
            Long totalCount = count + urlMapping.getClickCounter();
            return ShortenResponse.builder()
                    .shortCode(urlMapping.getShortCode())
                    .shortUrl(baseUrl + "/" + urlMapping.getShortCode())
                    .createdDate(urlMapping.getCreatedDate())
                    .expiresAt(urlMapping.getExpiresAt())
                    .originalUrl(urlMapping.getOriginalUrl())
                    .clickCounter(totalCount)
                    .build();
        }


        public ShortenResponse shorten(ShortenRequest shortenRequest)
        {
            String originalUrl = shortenRequest.getOriginalUrl();
            User currentUser = getCurrentUser();

            UrlMapping existing = null;
            if(currentUser != null)
            {
                existing = urlMappingRepo.findByOriginalUrlAndUser(originalUrl, currentUser).orElse(null);
            }
            if(existing != null)
            {
                return ShortenResponse.builder()
                        .shortUrl(baseUrl + "/" + existing.getShortCode())
                        .shortCode(existing.getShortCode())
                        .originalUrl(originalUrl)
                        .createdDate(existing.getCreatedDate())
                        .expiresAt(existing.getExpiresAt())
                        .clickCounter(existing.getClickCounter())
                        .build();
            }
            String shortCode = resolveCode(shortenRequest.getAlias());
            UrlMapping urlMapping = UrlMapping.builder()
                    .originalUrl(originalUrl)
                    .shortCode(shortCode)
                    .user(currentUser)
                    .build();
            UrlMapping saved = urlMappingRepo.save(urlMapping);
            return ShortenResponse.builder()
                    .shortUrl(baseUrl + "/" + shortCode)
                    .shortCode(shortCode)
                    .originalUrl(originalUrl)
                    .createdDate(saved.getCreatedDate())
                    .expiresAt(saved.getExpiresAt())
                    .clickCounter(saved.getClickCounter())
                    .build();
        }

        @Cacheable(key = "#code", value = "url")
        public String getOriginalUrl(String code)
        {
            System.out.println("DB called");
            UrlMapping urlMapping = urlMappingRepo.findByShortCode(code)
                    .orElseThrow(()->new ResourceNotFoundException("Url mapping not found"));
            if(LocalDateTime.now().isAfter(urlMapping.getExpiresAt()))
            {
                urlMappingRepo.delete(urlMapping);
                throw new ExpiryException("Your Url is expired at " +  urlMapping.getExpiresAt());
            }
            else
            {
                return urlMapping.getOriginalUrl();
            }
        }

        public List<ShortenResponse> getMyUrls()
        {
            User currentUser = getCurrentUser();
            return urlMappingRepo.findByUser(currentUser)
                    .stream()
                    .map(map -> ShortenResponse.builder()
                            .shortUrl(baseUrl + "/" + map.getShortCode())
                            .shortCode(map.getShortCode())
                            .createdDate(map.getCreatedDate())
                            .originalUrl(map.getOriginalUrl())
                            .expiresAt(map.getExpiresAt())
                            .clickCounter(map.getClickCounter())
                            .build())
                    .toList();
        }
        private User getCurrentUser()
        {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null || !authentication.isAuthenticated()
                    || authentication.getPrincipal().equals("anonymousUser"))
            {
                return null;
            }
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            return userRepo.findByEmail(email).orElse(null);
        }

        private String resolveCode(String code)
        {
            if(code != null && !code.isBlank())
            {
                String trimmedCode = code.trim();
                if(urlMappingRepo.existsByShortCode(trimmedCode))
                {
                    throw new DuplicateResourceException("Alias '" + trimmedCode + "' already exists try another one");
                }
                return trimmedCode;
            }
            String shortCode;
            do {
                shortCode = generateCode();
            } while(urlMappingRepo.existsByShortCode(shortCode));
            return shortCode;
        }

        private String generateCode()
        {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);
            for(int i = 0; i < CODE_LENGTH; i++)
            {
                sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
            }
            return sb.toString();
        }

        public byte[] generateQRCode(ShortenRequest shortenRequest)  {
            ShortenResponse shortenResponse = shorten(shortenRequest);
            String shortUrl =  shortenResponse.getShortUrl();
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = null;
            try {
                bitMatrix = qrCodeWriter.encode(shortUrl, BarcodeFormat.QR_CODE, 300, 300);
            } catch (WriterException e)
            {
                throw new RuntimeException("Failed to generate QR code");
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            } catch (IOException e)
            {
                throw new RuntimeException("Failed to generate QR code");
            }
            return outputStream.toByteArray();
        }

        @CacheEvict(key = "#shortCode", value = "url")
        public void deleteOne(String shortCode)
        {
            redisTemplate.delete("clickCount::" + shortCode);
            UrlMapping urlMapping = urlMappingRepo.findByShortCode(shortCode).orElseThrow(() -> new ResourceNotFoundException("URL not found"));
            User currentUser = getCurrentUser();
                if(urlMapping.getUser() == null || !urlMapping.getUser().equals(currentUser))
                {
                    throw new IllegalArgumentException("You are not authorized to delete this URL");
                }
            urlMappingRepo.delete(urlMapping);
            System.out.println("DB called");
        }

        public void deleteAll()
        {
            User user = getCurrentUser();
            if(user != null)
            {
                urlMappingRepo.deleteByUser(user);
            }
            else
            {
                throw new IllegalArgumentException("You must be logged in to delete URLs");
            }
        }

        public void incrementClickCount(String shortCode)
        {
            if(urlMappingRepo.existsByShortCode(shortCode))
            {
                redisTemplate.opsForValue().increment(
                        "clickCount::" + shortCode
                );
                System.out.println("incremented click count for " + shortCode);
            }
        }

    }
