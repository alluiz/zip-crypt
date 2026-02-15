package com.example.zipcrypt.service.impl;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryPasswordCacheServiceTest {

    @Test
    void shouldStoreAndRetrievePassword() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        InMemoryPasswordCacheService service = new InMemoryPasswordCacheService(clock);

        service.put("archive.zip", "secret".toCharArray());

        assertThat(service.get("archive.zip")).contains("secret");
    }

    @Test
    void shouldExpireAfterOneDay() {
        MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        InMemoryPasswordCacheService service = new InMemoryPasswordCacheService(clock);

        service.put("archive.zip", "secret".toCharArray());
        clock.setInstant(Instant.parse("2026-01-02T00:00:01Z"));

        assertThat(service.get("archive.zip")).isEmpty();
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        private void setInstant(Instant instant) {
            this.instant = instant;
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
