package com.uc.ms_security.dto.rolepermission;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// Estado final de la matriz: ids de TODOS los permisos que debe tener el rol.
@Getter
@Setter
public class SyncRolePermissionsRequestDTO {

    @NotNull(message = "La lista de permisos es obligatoria")
    private List<@NotNull(message = "El id del permiso no puede ser nulo") Long> permissionIds;
}
