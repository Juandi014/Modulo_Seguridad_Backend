package com.uc.ms_security.service;

import com.uc.ms_security.dto.auth.TwoFactorStatusDTO;

// Habilitar / deshabilitar el 2FA desde la pantalla Seguridad.
public interface ITwoFactorSettingsService {

    TwoFactorStatusDTO getStatus(Long userId);

    TwoFactorStatusDTO setEnabled(Long userId, boolean enabled, String currentPassword);
}
