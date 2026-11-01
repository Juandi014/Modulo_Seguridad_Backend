package com.uc.ms_security.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// Código de un solo uso que el backend entrega al frontend tras el login OAuth
@Getter
@Setter
public class OAuthExchangeRequestDTO {

    @NotBlank(message = "El código es obligatorio")
    private String code;
}
