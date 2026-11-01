package com.uc.ms_security.dto.rolepermission;

import com.uc.ms_security.dto.role.RoleResponseDTO;
import lombok.Value;

import java.util.List;

// Datos de la pantalla "Asignar permisos a roles": el rol, cuántos usuarios
// lo tienen y todos los permisos del sistema marcados según la asignación.
@Value
public class RolePermissionMatrixDTO {

    RoleResponseDTO role;

    long userCount;

    List<MatrixPermissionDTO> permissions;
}
