
package org.example.ticketSalesSystem.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;

    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getCachedResponse(String key) {
        return redisTemplate.opsForValue()
                .get("idempotency:" + key);
    }

    public boolean reserveKey(String key) {
        Boolean wasSet = redisTemplate.opsForValue().setIfAbsent(
                "idempotency:" + key,
                "PROCESSING",
                Duration.ofMinutes(10)
        );

        return Boolean.TRUE.equals(wasSet);
    }

    public void saveResponse(String key, String response) {
        redisTemplate.opsForValue().set(
                "idempotency:" + key,
                response,
                Duration.ofMinutes(10)
        );
    }

    public void removeKey(String key) {
        redisTemplate.delete("idempotency:" + key);
    }
}
