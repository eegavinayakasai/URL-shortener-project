package com.evs.UrlShortenerProject.scheduler;

import com.evs.UrlShortenerProject.model.UrlMapping;
import com.evs.UrlShortenerProject.repo.UrlMappingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ClickCounterScheduler
{
    private final StringRedisTemplate redisTemplate;
    private final UrlMappingRepo urlMappingRepo;

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void syncClickCounters()
    {
        Set<String> keys = redisTemplate.keys("clickCount::*");

        if(keys == null || keys.isEmpty())
        {
            return;
        }

        for(String key : keys)
        {
            Object value = redisTemplate.opsForValue().get(key);

            long count = 0;

            if(value != null)
            {
                count = Long.parseLong(value.toString());
            }

            String shortCode = key.replace("clickCount::", "");

            UrlMapping urlMapping = urlMappingRepo.findByShortCode(shortCode)
                    .orElse(null);

            if(urlMapping != null)
            {
                urlMapping.setClickCounter(
                        urlMapping.getClickCounter() + count
                );

                urlMappingRepo.save(urlMapping);
            }

            redisTemplate.delete(key);
        }
    }
}