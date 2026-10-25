package com.uc.ms_security.service.impl;

import com.uc.ms_security.dto.AuthResponseDTO;
import com.uc.ms_security.service.IOAuthExchangeCodeStore;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// Guarda los códigos en memoria durante 60 segundos.
@Service
public class OAuthExchangeCodeStoreImpl implements IOAuthExchangeCodeStore {

    private static final Duration CODE_DURATION = Duration.ofSeconds(60);

    private final SecureRandom random = new SecureRandom();
    private final Map<String, Entry> codes = new ConcurrentHashMap<>();

    @Override
    public String save(AuthResponseDTO session) {
        codes.values().removeIf(Entry::expired);
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String code = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        codes.put(code, new Entry(session, Instant.now().plus(CODE_DURATION)));
        return code;
    }

    @Override
    public Optional<AuthResponseDTO> consume(String code) {
        Entry entry = codes.remove(code); // un solo uso
        if (entry == null || entry.expired()) {
            return Optional.empty();
        }
        return Optional.of(entry.session());
    }

    private record Entry(AuthResponseDTO session, Instant expiresAt) {
        boolean expired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
