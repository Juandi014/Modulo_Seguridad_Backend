package com.uc.ms_security.dto.common;

import java.util.List;

// Página de resultados para listados grandes
public record PageResponseDTO<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
