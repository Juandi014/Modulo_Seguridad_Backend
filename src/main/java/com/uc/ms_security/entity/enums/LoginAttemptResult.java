package com.uc.ms_security.entity.enums;

// Resultado de un intento de inicio de sesión (las etiquetas en español
// se muestran en el frontend).
public enum LoginAttemptResult {
    SUCCESS,                // Exitoso: sesión abierta
    PENDING_2FA,            // Contraseña correcta, código 2FA enviado
    INVALID_CREDENTIALS,    // Correo o contraseña incorrectos
    USER_INACTIVE,          // Usuario desactivado
    EXTERNAL_ACCOUNT,       // Cuenta OAuth intentó entrar con contraseña
    INVALID_2FA_CODE,       // Código 2FA incorrecto
    EXPIRED_2FA_CODE,       // Código 2FA vencido
    TOO_MANY_2FA_ATTEMPTS,  // Demasiados códigos incorrectos
    OAUTH_REJECTED          // El proveedor o las reglas OAuth rechazaron el acceso
}
