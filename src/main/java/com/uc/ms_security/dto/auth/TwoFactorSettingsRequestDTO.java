package com.uc.ms_security.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

// Habilitar o deshabilitar el 2FA exige confirmar la contraseña actual
@Getter
@Setter
public class TwoFactorSettingsRequestDTO {

    @NotNull(message = "Indique si desea habilitar o deshabilitar el 2FA")
    private Boolean enabled;

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String currentPassword;
}
