package com.uc.ms_security.service;

import com.uc.ms_security.dto.auth.OAuthProfileDTO;
import com.uc.ms_security.dto.auth.AuthResponseDTO;

import java.util.List;

// Inicio de sesión con proveedores externos (Google, GitHub, Facebook).
public interface IOAuthLoginService {

    /** Identifica o crea el usuario del perfil OAuth y abre su sesión interna. */
    AuthResponseDTO login(OAuthProfileDTO profile);

    /** Cambia el código de un solo uso por la sesión ya creada. */
    AuthResponseDTO exchange(String code);

    List<String> enabledProviders();
}
