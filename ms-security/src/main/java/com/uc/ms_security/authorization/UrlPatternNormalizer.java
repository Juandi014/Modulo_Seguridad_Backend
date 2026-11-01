package com.uc.ms_security.authorization;

import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Lleva URLs y métodos a un formato único, para que el permiso guardado
 * y la petición real se comparen igual:
 *   /api/users/15          -> /api/users/{id}
 *   /api/users/{userId}/   -> /api/users/{id}
 *   patch                  -> PATCH
 */
@Component
public class UrlPatternNormalizer {

    public String normalizeUrl(String url) {
        String normalized = url.trim()
                .replaceAll("\\{[^/}]+}", "{id}")
                .replaceAll("(?i)(?<=/)(?:\\d+|[a-f0-9]{24})(?=/|$)", "{id}");
        if (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    public String normalizeMethod(String method) {
        return method.trim().toUpperCase(Locale.ROOT);
    }
}
