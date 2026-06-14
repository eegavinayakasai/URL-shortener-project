package com.evs.UrlShortenerProject.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class UrlMapping
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,name="original_url",length = 2048)
    private String originalUrl;

    @Column(unique = true, nullable = false,name="short_code")
    private String shortCode;

    @Column(name="created_date")
    private LocalDateTime createdDate;

    @Column(name="expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    public void prePersistExpires()
    {
        this.createdDate = LocalDateTime.now();
        this.expiresAt = createdDate.plusDays(1);
    }

    @Column(name="click_counter",nullable = false)
    @Builder.Default
    private Long clickCounter = 0L;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
