package com.uc.ms_security.service;

import com.uc.ms_security.dto.auth.AuthResponseDTO;

import java.util.Optional;

/**
 * Códigos de un solo uso para entregar la sesión OAuth al frontend sin
 * poner el token en la URL: el backend redirige con ?code=... y el
 * frontend lo cambia por el token (una sola vez, en pocos segundos).
 */
public interface IOAuthExchangeCodeStore {

    String save(AuthResponseDTO session);

    Optional<AuthResponseDTO> consume(String code);
}
