package com.uc.ms_security.service;

import com.uc.ms_security.dto.auth.RegisterRequestDTO;
import com.uc.ms_security.dto.user.UserResponseDTO;

public interface IRegistrationService {

    /** Registra una cuenta con correo y contraseña (autenticación tradicional). */
    UserResponseDTO register(RegisterRequestDTO dto);
}
