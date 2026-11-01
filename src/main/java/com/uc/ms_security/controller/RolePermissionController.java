package com.uc.ms_security.controller;

import com.uc.ms_security.authorization.AuthenticatedUserId;
import com.uc.ms_security.dto.rolepermission.AssignPermissionRequestDTO;
import com.uc.ms_security.dto.rolepermission.PermissionRoleResponseDTO;
import com.uc.ms_security.dto.rolepermission.RolePermissionMatrixDTO;
import com.uc.ms_security.dto.rolepermission.RolePermissionResponseDTO;
import com.uc.ms_security.dto.rolepermission.SyncRolePermissionsRequestDTO;
import com.uc.ms_security.service.IRolePermissionMatrixService;
import com.uc.ms_security.service.IRolePermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {

    private final IRolePermissionService rolePermissionService;
    private final IRolePermissionMatrixService rolePermissionMatrixService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RolePermissionResponseDTO assign(
            @Valid @RequestBody AssignPermissionRequestDTO dto) {
        return rolePermissionService.assign(dto);
    }

    @GetMapping
    public List<RolePermissionResponseDTO> findAll() {
        return rolePermissionService.findAll();
    }

    @GetMapping("/roles/{roleId}")
    public List<RolePermissionResponseDTO> findByRoleId(@PathVariable Long roleId) {
        return rolePermissionService.findByRoleId(roleId);
    }

    // Matriz: todos los permisos del sistema, marcados según el rol
    @GetMapping("/roles/{roleId}/matrix")
    public RolePermissionMatrixDTO getMatrix(@PathVariable Long roleId) {
        return rolePermissionMatrixService.getMatrix(roleId);
    }

    // Guardar cambios de la matriz: el rol queda con exactamente estos permisos
    @PutMapping("/roles/{roleId}")
    public RolePermissionMatrixDTO savePermissions(
            @PathVariable Long roleId,
            @Valid @RequestBody SyncRolePermissionsRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {
        return rolePermissionMatrixService.savePermissions(
                roleId, dto, AuthenticatedUserId.from(jwt));
    }

    @GetMapping("/permissions/{permissionId}")
    public List<PermissionRoleResponseDTO> findByPermissionId(
            @PathVariable Long permissionId) {
        return rolePermissionService.findByPermissionId(permissionId);
    }

    @DeleteMapping("/{roleId}/{permissionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        rolePermissionService.delete(roleId, permissionId);
    }
}
