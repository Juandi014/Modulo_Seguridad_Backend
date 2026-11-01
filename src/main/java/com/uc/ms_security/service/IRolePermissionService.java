package com.uc.ms_security.service;

import com.uc.ms_security.dto.rolepermission.AssignPermissionRequestDTO;
import com.uc.ms_security.dto.rolepermission.PermissionRoleResponseDTO;
import com.uc.ms_security.dto.rolepermission.RolePermissionResponseDTO;

import java.util.List;

// Asignación individual de permisos a roles (una asociación a la vez).
public interface IRolePermissionService {

    RolePermissionResponseDTO assign(AssignPermissionRequestDTO dto);

    List<RolePermissionResponseDTO> findAll();

    List<RolePermissionResponseDTO> findByRoleId(Long roleId);

    List<PermissionRoleResponseDTO> findByPermissionId(Long permissionId);

    void delete(Long roleId, Long permissionId);
}
