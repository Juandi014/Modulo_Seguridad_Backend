package com.uc.ms_security.dto.auth;

import com.uc.ms_security.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// Nueva contraseña con la misma regla de seguridad del sistema (@ValidPassword)
@Getter
@Setter
public class PasswordResetRequestDTO {

    @NotBlank(message = "El enlace de recuperación no es válido")
    private String token;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @ValidPassword
    private String newPassword;
}
