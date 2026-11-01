package com.uc.ms_security.dto.auth;

import com.uc.ms_security.dto.user.BaseUserDTO;
import com.uc.ms_security.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// Registro público con credenciales: nombre y correo (heredados) + contraseña.
// Usa la misma regla de contraseña que la creación de usuarios (@ValidPassword).
@Getter
@Setter
public class RegisterRequestDTO extends BaseUserDTO {

    @NotBlank(
            message = "La contraseña es obligatoria"
    )
    @ValidPassword
    private String password;
}
