package com.uc.ms_security.service;

// Recuperación de contraseña por correo (solo cuentas con correo y contraseña).
public interface IPasswordRecoveryService {

    /** Envía el enlace de recuperación si corresponde. No revela si el correo existe. */
    void requestRecovery(String email);

    /** Verifica que el enlace siga siendo válido (lanza error si no lo es). */
    void validateToken(String token);

    /** Establece la nueva contraseña y cierra todas las sesiones del usuario. */
    void resetPassword(String token, String newPassword);
}
