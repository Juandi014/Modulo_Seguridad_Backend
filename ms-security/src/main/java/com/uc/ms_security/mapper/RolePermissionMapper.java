package com.uc.ms_security.mapper;

import com.uc.ms_security.dto.role.RoleResponseDTO;
import com.uc.ms_security.dto.rolepermission.MatrixPermissionDTO;
import com.uc.ms_security.dto.rolepermission.PermissionRoleResponseDTO;
import com.uc.ms_security.dto.rolepermission.RolePermissionResponseDTO;
import com.uc.ms_security.entity.Permission;
import com.uc.ms_security.entity.Role;
import com.uc.ms_security.entity.RolePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RolePermissionMapper {

    private final PermissionMapper permissionMapper;

    public RolePermissionResponseDTO toResponseDTO(RolePermission rolePermission) {
        return new RolePermissionResponseDTO(
                rolePermission.getId(),
                rolePermission.getRole().getId(),
                permissionMapper.toResponseDTO(rolePermission.getPermission())
        );
    }

    public List<RolePermissionResponseDTO> toResponseDTOList(
            List<RolePermission> rolePermissions) {
        return rolePermissions.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public PermissionRoleResponseDTO toPermissionRoleResponseDTO(
            RolePermission rolePermission) {
        Role role = rolePermission.getRole();
        return new PermissionRoleResponseDTO(
                rolePermission.getId(),
                rolePermission.getPermission().getId(),
                new RoleResponseDTO(
                        role.getId(),
                        role.getName(),
                        role.getDescription(),
                        role.isActive()
                )
        );
    }

    public MatrixPermissionDTO toMatrixPermissionDTO(Permission permission, boolean assigned) {
        return new MatrixPermissionDTO(
                permission.getId(),
                permission.getModule(),
                permission.getResource(),
                permission.getOperation(),
                permission.getMethod(),
                permission.getUrl(),
                assigned
        );
    }

    public List<PermissionRoleResponseDTO> toPermissionRoleResponseDTOList(
            List<RolePermission> rolePermissions) {
        return rolePermissions.stream()
                .map(this::toPermissionRoleResponseDTO)
                .toList();
    }
}
