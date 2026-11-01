package com.uc.ms_security.service.impl;

import com.uc.ms_security.dto.rolepermission.MatrixPermissionDTO;
import com.uc.ms_security.dto.rolepermission.RolePermissionMatrixDTO;
import com.uc.ms_security.dto.rolepermission.SyncRolePermissionsRequestDTO;
import com.uc.ms_security.entity.Permission;
import com.uc.ms_security.entity.Role;
import com.uc.ms_security.entity.RolePermission;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.mapper.RoleMapper;
import com.uc.ms_security.mapper.RolePermissionMapper;
import com.uc.ms_security.repository.PermissionRepository;
import com.uc.ms_security.repository.RolePermissionRepository;
import com.uc.ms_security.repository.RoleRepository;
import com.uc.ms_security.repository.UserRoleRepository;
import com.uc.ms_security.service.IRolePermissionMatrixService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionMatrixServiceImpl implements IRolePermissionMatrixService {

    // Endpoints con los que se gestiona esta misma matriz. Nadie puede
    // quitárselos a sí mismo, para no quedar sin forma de corregirlo.
    private static final String MATRIX_URL = "/api/role-permissions/roles/{id}/matrix";
    private static final String SAVE_URL = "/api/role-permissions/roles/{id}";

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional(readOnly = true)
    public RolePermissionMatrixDTO getMatrix(Long roleId) {
        Role role = findRole(roleId);
        Set<Long> assignedIds = rolePermissionRepository.findAllByRoleId(roleId).stream()
                .map(rolePermission -> rolePermission.getPermission().getId())
                .collect(Collectors.toSet());

        List<MatrixPermissionDTO> permissions = permissionRepository
                .findAll(Sort.by("module", "resource", "operation", "url"))
                .stream()
                .map(permission -> rolePermissionMapper.toMatrixPermissionDTO(
                        permission,
                        assignedIds.contains(permission.getId())
                ))
                .toList();

        return new RolePermissionMatrixDTO(
                roleMapper.toResponseDTO(role),
                userRoleRepository.countByRoleId(roleId),
                permissions
        );
    }

    @Override
    @Transactional
    public RolePermissionMatrixDTO savePermissions(
            Long roleId,
            SyncRolePermissionsRequestDTO dto,
            Long currentUserId) {
        Role role = findRole(roleId);
        Set<Long> requestedIds = new HashSet<>(dto.getPermissionIds());

        Map<Long, Permission> requestedPermissions = permissionRepository
                .findAllById(requestedIds).stream()
                .collect(Collectors.toMap(Permission::getId, Function.identity()));
        if (requestedPermissions.size() != requestedIds.size()) {
            throw new ApplicationException(
                    ErrorCase.NOT_FOUND,
                    "Uno o más permisos seleccionados no existen"
            );
        }

        preventSelfLockout(roleId, requestedIds, currentUserId);

        List<RolePermission> current = rolePermissionRepository.findAllByRoleId(roleId);
        Set<Long> currentIds = current.stream()
                .map(rolePermission -> rolePermission.getPermission().getId())
                .collect(Collectors.toSet());

        // Retirar los desmarcados (el permiso sigue existiendo en el sistema)
        List<RolePermission> toRemove = current.stream()
                .filter(rolePermission -> !requestedIds.contains(rolePermission.getPermission().getId()))
                .toList();
        rolePermissionRepository.deleteAll(toRemove);

        // Agregar solo los nuevos: nunca se repite una asociación existente
        List<RolePermission> toAdd = requestedIds.stream()
                .filter(id -> !currentIds.contains(id))
                .map(id -> {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRole(role);
                    rolePermission.setPermission(requestedPermissions.get(id));
                    return rolePermission;
                })
                .toList();
        rolePermissionRepository.saveAll(toAdd);
        rolePermissionRepository.flush();

        return getMatrix(roleId);
    }

    // Si el rol pertenece al usuario que edita, no puede quitarle los permisos
    // para ver y guardar esta matriz, salvo que los tenga por otro rol activo.
    private void preventSelfLockout(Long roleId, Set<Long> requestedIds, Long currentUserId) {
        if (!userRoleRepository.existsByUserIdAndRoleId(currentUserId, roleId)) {
            return;
        }
        List<Permission> criticalPermissions = List.of(
                        permissionRepository.findByUrlAndMethod(MATRIX_URL, "GET"),
                        permissionRepository.findByUrlAndMethod(SAVE_URL, "PUT"))
                .stream()
                .flatMap(java.util.Optional::stream)
                .toList();

        for (Permission permission : criticalPermissions) {
            boolean keepsIt = requestedIds.contains(permission.getId())
                    || rolePermissionRepository.userHasPermissionThroughOtherRole(
                            currentUserId, permission.getId(), roleId);
            if (!keepsIt) {
                throw new ApplicationException(
                        ErrorCase.INVALID_OPERATION,
                        "No puede quitar a su propio rol el permiso para gestionar "
                                + "los permisos de roles (" + permission.getMethod()
                                + " " + permission.getUrl() + ")"
                );
            }
        }
    }

    private Role findRole(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Rol no encontrado"
                ));
    }
}
