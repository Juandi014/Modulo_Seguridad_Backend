package com.uc.ms_security.service;

import com.uc.ms_security.dto.rolepermission.RolePermissionMatrixDTO;
import com.uc.ms_security.dto.rolepermission.SyncRolePermissionsRequestDTO;

// Matriz de permisos de un rol (pantalla del mockup HU-SE-06).
public interface IRolePermissionMatrixService {

    RolePermissionMatrixDTO getMatrix(Long roleId);

    /** Deja al rol exactamente con los permisos indicados (agrega y retira). */
    RolePermissionMatrixDTO savePermissions(
            Long roleId,
            SyncRolePermissionsRequestDTO dto,
            Long currentUserId
    );
}
