package com.uc.ms_security.entity.enums;

// Mecanismo con el que se creó y se autentica la cuenta.
// LOCAL = correo y contraseña de la plataforma; el resto son proveedores OAuth.
public enum AuthProvider {
    LOCAL,
    GOOGLE,
    GITHUB,
    FACEBOOK
}
