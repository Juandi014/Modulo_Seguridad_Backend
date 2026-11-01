package com.uc.ms_security.entity.enums;

// PENDING_2FA: contraseña correcta, falta el segundo factor (sin token).
// ACTIVE: autenticación completa; la sesión tiene token y da acceso.
public enum SessionStatus {
    PENDING_2FA,
    ACTIVE
}
