package com.evs.UrlShortenerProject.repo;

import com.evs.UrlShortenerProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long>
{
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByEmailOrUsername(String email, String username);
    Optional<User> findByEmail(String email);
}
