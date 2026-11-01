package com.uc.ms_security.dto.profile;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRequestDTO {

    // Opcional: vacío o entre 7 y 20 caracteres con números, espacios, +, ( ) y -
    @Pattern(
            regexp = "^$|^[0-9+()\\- ]{7,20}$",
            message = "El teléfono debe tener entre 7 y 20 caracteres "
                    + "y solo puede contener números, espacios, +, paréntesis y guiones"
    )
    private String phone;
}
