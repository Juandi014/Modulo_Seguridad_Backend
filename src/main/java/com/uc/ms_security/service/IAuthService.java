package com.uc.ms_security.service;

import com.uc.ms_security.dto.auth.LoginRequestDTO;
import com.uc.ms_security.dto.auth.LoginResponseDTO;

// Autenticación tradicional con correo y contraseña, y cierre de sesión.
public interface IAuthService {

    /** Sesión completa, o el reto 2FA si el usuario lo tiene habilitado. */
    LoginResponseDTO login(LoginRequestDTO dto);

    void logout(String tokenId);
}
