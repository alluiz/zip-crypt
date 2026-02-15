package com.example.zipcrypt.service.impl;

import com.example.zipcrypt.service.PasswordCacheService;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryPasswordCacheService implements PasswordCacheService {

    private static final Duration TTL = Duration.ofDays(1);

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final Clock clock;

    public InMemoryPasswordCacheService() {
        this(Clock.systemUTC());
    }

    InMemoryPasswordCacheService(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void put(String fileName, char[] password) {
        cleanupExpired();
        cache.put(fileName, new CacheEntry(new String(password), Instant.now(clock).plus(TTL)));
    }

    @Override
    public Optional<String> get(String fileName) {
        cleanupExpired();
        CacheEntry entry = cache.get(fileName);
        if (entry == null || entry.expiresAt().isBefore(Instant.now(clock))) {
            cache.remove(fileName);
            return Optional.empty();
        }
        return Optional.of(entry.password());
    }

    private void cleanupExpired() {
        Instant now = Instant.now(clock);
        cache.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
    }

    private record CacheEntry(String password, Instant expiresAt) {}
}
