package com.uc.ms_security.dto.permission;

import com.uc.ms_security.entity.enums.PermissionModule;
import com.uc.ms_security.entity.enums.PermissionOperation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRequestDTO {

    @NotNull(message = "El módulo es obligatorio")
    private PermissionModule module;

    @NotBlank(message = "El recurso es obligatorio")
    @Size(max = 50, message = "El recurso no puede superar 50 caracteres")
    private String resource;

    @NotNull(message = "La operación es obligatoria")
    private PermissionOperation operation;

    @NotBlank(message = "La url es obligatoria")
    @Size(max = 255, message = "La url no puede superar 255 caracteres")
    @Pattern(regexp = "^/\\S*$", message = "La url debe iniciar con / y no tener espacios")
    private String url;

    @NotBlank(message = "El método es obligatorio")
    @Pattern(
            regexp = "(?i)^(GET|POST|PUT|PATCH|DELETE)$",
            message = "El método debe ser GET, POST, PUT, PATCH o DELETE"
    )
    private String method;
}
