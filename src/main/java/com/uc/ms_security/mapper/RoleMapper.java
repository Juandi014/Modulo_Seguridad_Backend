package com.uc.ms_security.mapper;

import com.uc.ms_security.dto.role.RoleRequestDTO;
import com.uc.ms_security.dto.role.RoleResponseDTO;
import com.uc.ms_security.dto.rolepermission.RolePermissionsResponseDTO;
import com.uc.ms_security.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    private final RolePermissionMapper rolePermissionMapper;

    public Role toEntity(RoleRequestDTO dto) {
        Role role = new Role();
        role.setName(dto.getName().trim());
        role.setDescription(dto.getDescription().trim());
        return role;
    }

    public void updateEntity(RoleRequestDTO dto, Role role) {
        role.setName(dto.getName().trim());
        role.setDescription(dto.getDescription().trim());
    }

    public RoleResponseDTO toResponseDTO(Role role) {
        return new RoleResponseDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.isActive()
        );
    }

    public List<RoleResponseDTO> toResponseDTOList(List<Role> roles) {
        return roles.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public RolePermissionsResponseDTO toPermissionsResponseDTO(Role role) {
        return new RolePermissionsResponseDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                rolePermissionMapper.toResponseDTOList(role.getRolePermissions())
        );
    }
}