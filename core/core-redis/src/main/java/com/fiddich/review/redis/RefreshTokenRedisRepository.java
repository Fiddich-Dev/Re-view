package com.fiddich.review.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

    private static final String KEY_PREFIX = "refresh:";
    private static final Duration TTL = Duration.ofDays(7);

    private final StringRedisTemplate redisTemplate;

    public void save(String token, Long userId) {
        redisTemplate.opsForValue().set(KEY_PREFIX + token, String.valueOf(userId), TTL);
    }

    public Optional<Long> findUserIdByToken(String token) {
        String value = redisTemplate.opsForValue().get(KEY_PREFIX + token);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(Long.parseLong(value));
    }

    /**
     * 토큰을 조회하고 즉시 삭제한다 (GETDEL — 원자적 연산).
     * refresh() 흐름에서 race condition 방지.
     */
    public Optional<Long> getAndDelete(String token) {
        String value = redisTemplate.opsForValue().getAndDelete(KEY_PREFIX + token);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(Long.parseLong(value));
    }

    public void delete(String token) {
        redisTemplate.delete(KEY_PREFIX + token);
    }
}
