package com.uc.ms_security.dto.audit;

import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.enums.LoginAttemptResult;

import java.time.LocalDateTime;

public record LoginAttemptResponseDTO(
        Long id,
        String email,
        Long userId,
        String userName,
        AuthProvider method,
        LoginAttemptResult result,
        String ipAddress,
        LocalDateTime createdAt) {
}
