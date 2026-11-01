package com.uc.ms_security.service;

import com.uc.ms_security.dto.common.PageResponseDTO;
import com.uc.ms_security.dto.audit.LoginAttemptResponseDTO;
import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.enums.LoginAttemptResult;
import com.uc.ms_security.entity.User;

// Auditoría de inicios de sesión: registrar cada intento y consultarlos.
public interface ILoginAuditService {

    /** Registra un intento. user puede ser null si no se identificó a nadie. */
    void record(String email, User user, AuthProvider method, LoginAttemptResult result);

    PageResponseDTO<LoginAttemptResponseDTO> search(
            String email,
            LoginAttemptResult result,
            int page,
            int size
    );
}
