package kz.lab.moderationapp.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;

import kz.lab.moderationapp.model.ReportEvent;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RedisService {
    @Autowired
    private final ReactiveRedisOperations<String, ReportEvent> redisOperations;

    public Mono<Boolean> saveEvent(String key, ReportEvent event, long ttl) {
        return redisOperations.opsForValue()
                .set(key, event, Duration.ofSeconds(ttl));
    }

    public Mono<ReportEvent> getEvent(String key) {
        return redisOperations.opsForValue().get("event:" + key);
    }

    public Mono<Boolean> isAlreadyProcessed(String key) {
        return redisOperations.hasKey(key);
    }
}
