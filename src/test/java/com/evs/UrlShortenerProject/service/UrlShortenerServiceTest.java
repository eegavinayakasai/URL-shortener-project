    package com.evs.UrlShortenerProject.service;

    import com.evs.UrlShortenerProject.dto.ShortenRequest;
    import com.evs.UrlShortenerProject.dto.ShortenResponse;
    import com.evs.UrlShortenerProject.exceptionHandler.ExpiryException;
    import com.evs.UrlShortenerProject.exceptionHandler.ResourceNotFoundException;
    import com.evs.UrlShortenerProject.model.UrlMapping;
    import com.evs.UrlShortenerProject.model.User;
    import com.evs.UrlShortenerProject.repo.UrlMappingRepo;
    import com.evs.UrlShortenerProject.repo.UserRepo;
    import org.junit.jupiter.api.AfterEach;
    import org.junit.jupiter.api.Test;
    import org.junit.jupiter.api.extension.ExtendWith;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.junit.jupiter.MockitoExtension;
    import org.springframework.data.redis.core.StringRedisTemplate;
    import org.springframework.data.redis.core.ValueOperations;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.time.LocalDateTime;
    import java.util.Optional;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
    class UrlShortenerServiceTest
    {
        @Mock
        private UrlMappingRepo urlMappingRepo;

        @Mock
        private UserRepo userRepo;

        @Mock
        private StringRedisTemplate stringRedisTemplate;

        @Mock
        private ValueOperations<String, String> valueOperations;

        @InjectMocks
        private UrlShortenerService urlShortenerService;

        @Mock
        private Authentication authentication;

        @Mock
        private UserDetails userDetails;

        @Test
        void shouldReturnOriginalUrlWhenUrlIsNotExpired()
        {
            UrlMapping urlMapping = createUrlMapping(LocalDateTime.now().plusDays(1));
            when(urlMappingRepo.findByShortCode("abc123"))
                    .thenReturn(Optional.of(urlMapping));
            String result =  urlShortenerService.getOriginalUrl("abc123");
            assertEquals("https://google.com", result);
            verify(urlMappingRepo)
                    .findByShortCode("abc123");
            verify(urlMappingRepo, never()).delete(any());
        }

        @Test
        void shouldThrowExceptionWhenUrlIsExpired()
        {
            UrlMapping urlMapping = createUrlMapping(LocalDateTime.now().minusDays(1));
            when(urlMappingRepo.findByShortCode("abc123"))
                    .thenReturn(Optional.of(urlMapping));
            ExpiryException expiryException = assertThrows(ExpiryException.class, () -> urlShortenerService.getOriginalUrl("abc123"));
            assertTrue(expiryException.getMessage().contains("expired"));
            verify(urlMappingRepo)
                    .findByShortCode("abc123");
            verify(urlMappingRepo).delete(urlMapping);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenUrlIsNotFound()
        {
            when(urlMappingRepo.findByShortCode("otherCode"))
                    .thenReturn(Optional.empty());
            ResourceNotFoundException resourceNotFoundException = assertThrows
                    (ResourceNotFoundException.class,
                            () -> urlShortenerService.getOriginalUrl("otherCode"));
            assertTrue(resourceNotFoundException.getMessage().contains("not found"));
            verify(urlMappingRepo)
                    .findByShortCode("otherCode");
            verify(urlMappingRepo, never()).delete(any());
        }

        @Test
        void shouldReturnStatsWhenRedisContainsClickCount()
        {
            UrlMapping urlMapping = createUrlMapping(LocalDateTime.now().plusDays(1));
            when(urlMappingRepo.findByShortCode("abc123"))
                    .thenReturn(Optional.of(urlMapping));
            when(stringRedisTemplate.opsForValue())
                    .thenReturn(valueOperations);
            when(valueOperations.get("clickCount::abc123"))
                    .thenReturn("5");
            ShortenResponse response = urlShortenerService.getStats("abc123");
            assertEquals(5L, response.getClickCounter());
            assertEquals("abc123", response.getShortCode());
            assertEquals("https://google.com", response.getOriginalUrl());
            verify(urlMappingRepo).findByShortCode("abc123");
            verify(stringRedisTemplate).opsForValue();
            verify(valueOperations).get("clickCount::abc123");
        }

        @Test
        void shouldReturnStatsWhenRedisValueIsNull()
        {
            UrlMapping urlMapping = createUrlMapping(LocalDateTime.now().plusDays(1));
            when(urlMappingRepo.findByShortCode("abc123"))
                    .thenReturn(Optional.of(urlMapping));
            when(stringRedisTemplate.opsForValue())
                    .thenReturn(valueOperations);
            when(valueOperations.get("clickCount::abc123"))
                    .thenReturn(null);
            ShortenResponse response = urlShortenerService.getStats("abc123");
            assertEquals(0L, response.getClickCounter());
            assertEquals("abc123", response.getShortCode());
            assertEquals("https://google.com", response.getOriginalUrl());
            verify(urlMappingRepo).findByShortCode("abc123");
            verify(stringRedisTemplate).opsForValue();
            verify(valueOperations).get("clickCount::abc123");
        }

        @Test
        void shouldThrowResourceNotFoundExceptionIfCodeDoesNotExists()
        {
            when(urlMappingRepo.findByShortCode("abc123"))
                    .thenReturn(Optional.empty());
            ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class ,
                    () -> urlShortenerService.getStats("abc123"));
            assertTrue(resourceNotFoundException.getMessage().contains("not found"));
            verify(urlMappingRepo)
                    .findByShortCode("abc123");
            verify(stringRedisTemplate,never()).opsForValue();
            verify(valueOperations,never()).get("clickCount::abc123");
        }

        @Test
        void shouldDeleteUrlMappingIfItExists()
        {
            UrlMapping urlMapping = createUrlMapping(LocalDateTime.now().plusDays(1));
            User user = CreateUser("dummyEmail","dummyUser");
            urlMapping.setUser(user);
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
            when(urlMappingRepo.findByShortCode("abc123"))
                    .thenReturn(Optional.of(urlMapping));
            when(authentication.isAuthenticated())
                    .thenReturn(true);
            when(authentication.getPrincipal())
                    .thenReturn(userDetails);
            when(userDetails.getUsername())
                    .thenReturn("dummyEmail");
            when(userRepo.findByEmail("dummyEmail"))
                    .thenReturn(Optional.of(user));
            urlShortenerService.deleteOne("abc123");
            verify(urlMappingRepo).findByShortCode("abc123");
            verify(stringRedisTemplate).delete("clickCount::abc123");
            verify(userRepo)
                    .findByEmail("dummyEmail");
            verify(urlMappingRepo).delete(urlMapping);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionIfNotExistsAndShouldNotDelete()
        {
            when(urlMappingRepo.findByShortCode("abc123"))
                    .thenReturn(Optional.empty());
            ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class,
                    () -> urlShortenerService.deleteOne("abc123"));
            assertTrue(resourceNotFoundException.getMessage().contains("not found"));
            verify(urlMappingRepo).findByShortCode("abc123");
            verify(stringRedisTemplate).delete("clickCount::abc123");
            verify(urlMappingRepo, never()).delete(any());
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenUserIsNotAuthorizedAndShouldNotDelete()
        {
            UrlMapping urlMapping = createUrlMapping(LocalDateTime.now().plusDays(1));
            User owner = CreateUser("dummyOwnerEmail", "dummyOwnerUser");
            User currentUser = CreateUser("dummyCurrentUserEmail", "dummyCurrentUser");
            urlMapping.setUser(owner);
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
            when(urlMappingRepo.findByShortCode("abc123"))
                    .thenReturn(Optional.of(urlMapping));
            when(authentication.isAuthenticated())
                    .thenReturn(true);
            when(authentication.getPrincipal())
                    .thenReturn(userDetails);
            when(userDetails.getUsername())
                    .thenReturn(currentUser.getEmail());
            when(userRepo.findByEmail("dummyCurrentUserEmail"))
                    .thenReturn(Optional.of(currentUser));
            IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                    () ->  urlShortenerService.deleteOne("abc123"));
            assertTrue(illegalArgumentException.getMessage().contains("not authorized"));
            verify(stringRedisTemplate).delete("clickCount::abc123");
            verify(urlMappingRepo).findByShortCode("abc123");
            verify(urlMappingRepo, never()).delete(any());
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenUserIsNullAndShouldNotDelete()
        {
            UrlMapping urlMapping = createUrlMapping(LocalDateTime.now().plusDays(1));
            User currentUser = CreateUser("dummyCurrentUserEmail", "dummyCurrentUser");
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
            when(urlMappingRepo.findByShortCode("abc123"))
                    .thenReturn(Optional.of(urlMapping));
            when(authentication.isAuthenticated())
                    .thenReturn(true);
            when(authentication.getPrincipal())
                    .thenReturn(userDetails);
            when(userDetails.getUsername())
                    .thenReturn(currentUser.getEmail());
            when(userRepo.findByEmail("dummyCurrentUserEmail"))
                    .thenReturn(Optional.of(currentUser));
            assertNull(urlMapping.getUser());
            IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                    () -> urlShortenerService.deleteOne("abc123"));
            assertTrue(illegalArgumentException.getMessage().contains("not authorized"));
            verify(stringRedisTemplate).delete("clickCount::abc123");
            verify(urlMappingRepo).findByShortCode("abc123");
            verify(urlMappingRepo, never()).delete(any());
        }

        @Test
        void shouldReturnShortenResponseIfAlreadyExistsInDatabase()
        {
            String originalUrl = "https://google.com";
            String alias = "abc123";
            ShortenRequest shortenRequest = new ShortenRequest(originalUrl, alias);

            User currentUser = CreateUser("dummyEmail", "dummyUser");
            UrlMapping urlMapping = createUrlMapping(LocalDateTime.now().plusDays(1));
            urlMapping.setUser(currentUser);
            urlMapping.setOriginalUrl(originalUrl);
            urlMapping.setShortCode(alias);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("dummyEmail");
            when(userRepo.findByEmail("dummyEmail")).thenReturn(Optional.of(currentUser));

            when(urlMappingRepo.findByOriginalUrlAndUser(originalUrl, currentUser))
                    .thenReturn(Optional.of(urlMapping));

            ShortenResponse shortenResponse = urlShortenerService.shorten(shortenRequest);

            assertEquals(originalUrl, shortenResponse.getOriginalUrl());
            assertEquals(alias, shortenResponse.getShortCode());

            verify(urlMappingRepo).findByOriginalUrlAndUser(originalUrl, currentUser);
            verify(urlMappingRepo, never()).save(any());
        }

        @Test
        void shouldSaveAndReturnShortenResponseIfNotExistsInDatabase()
        {
            String originalUrl = "https://google.com";
            String alias = "abc123";
            ShortenRequest shortenRequest = new ShortenRequest(originalUrl, alias);
            User currentUser = CreateUser("dummyEmail", "dummyUser");
            UrlMapping urlMapping = createUrlMapping(null);
            urlMapping.setUser(currentUser);
            urlMapping.setOriginalUrl(originalUrl);
            urlMapping.setShortCode(alias);
            String trimmedCode = alias.trim();

            SecurityContextHolder.getContext().setAuthentication(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("dummyEmail");
            when(userRepo.findByEmail("dummyEmail")).thenReturn(Optional.of(currentUser));
            when(urlMappingRepo.findByOriginalUrlAndUser(originalUrl, currentUser))
                    .thenReturn(Optional.empty());
            when(urlMappingRepo.save(urlMapping)).thenReturn(urlMapping);
            when(urlMappingRepo.existsByShortCode(trimmedCode)).thenReturn(false);

            ShortenResponse shortenResponse = urlShortenerService.shorten(shortenRequest);

            assertEquals(originalUrl, shortenResponse.getOriginalUrl());
            assertEquals(alias, shortenResponse.getShortCode());
            assertNotNull(shortenResponse.getShortUrl());

            verify(urlMappingRepo).findByOriginalUrlAndUser(originalUrl, currentUser);
            verify(urlMappingRepo).save(urlMapping);

        }

        @AfterEach
        void tearDown() {
            SecurityContextHolder.clearContext();
        }

        private UrlMapping createUrlMapping(LocalDateTime expiresAt)
        {
            return UrlMapping.builder()
                    .shortCode("abc123")
                    .originalUrl("https://google.com")
                    .expiresAt(expiresAt)
                    .clickCounter(0L)
                    .build();
        }

        private User CreateUser(String email, String userName)
        {
            return User.builder()
                    .username(userName)
                    .email(email)
                    .build();
        }
    }