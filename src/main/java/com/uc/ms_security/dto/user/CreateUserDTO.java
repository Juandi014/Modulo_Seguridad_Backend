package com.uc.ms_security.dto.user;

import com.uc.ms_security.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserDTO  extends BaseUserDTO {

    @NotBlank(
            message = "La contraseña es obligatoria"
    )
    @ValidPassword
    private String password;
}
