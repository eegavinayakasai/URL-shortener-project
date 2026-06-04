package com.evs.UrlShortenerProject.repo;

import com.evs.UrlShortenerProject.model.UrlMapping;
import com.evs.UrlShortenerProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlMappingRepo extends JpaRepository<UrlMapping, Long>
{
    Optional<UrlMapping> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);

    List<UrlMapping> findByUser(User currentUser);
}
