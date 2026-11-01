package com.uc.ms_security.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TwoFactorCancelRequestDTO {

    @NotBlank(message = "El intento de inicio de sesión no es válido")
    private String challengeId;
}
