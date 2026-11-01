package com.uc.ms_security.entity.enums;

// Mecanismo con el que se creó y se autentica la cuenta.
// LOCAL = correo y contraseña de la plataforma; el resto son proveedores OAuth.
public enum AuthProvider {
    LOCAL("correo y contraseña"),
    GOOGLE("Google"),
    GITHUB("GitHub"),
    FACEBOOK("Facebook");

    // Nombre que se muestra al usuario en los mensajes
    private final String displayName;

    AuthProvider(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
