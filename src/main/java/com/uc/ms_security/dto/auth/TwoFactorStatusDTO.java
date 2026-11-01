package com.uc.ms_security.dto.auth;

// available = false para cuentas OAuth (el 2FA interno no les aplica)
public record TwoFactorStatusDTO(
        boolean enabled,
        boolean available) {
}
