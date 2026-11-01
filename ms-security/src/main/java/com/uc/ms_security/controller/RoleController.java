package com.uc.ms_security.controller;

import com.uc.ms_security.dto.role.RoleRequestDTO;
import com.uc.ms_security.dto.role.RoleResponseDTO;
import com.uc.ms_security.dto.rolepermission.RolePermissionsResponseDTO;
import com.uc.ms_security.service.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponseDTO create(@Valid @RequestBody RoleRequestDTO dto) {
        return roleService.create(dto);
    }

    @GetMapping
    public List<RoleResponseDTO> findAll() {
        return roleService.findAll();
    }

    @GetMapping("/{id}")
    public RoleResponseDTO findById(@PathVariable Long id) {
        return roleService.findById(id);
    }

    @GetMapping("/{id}/detail-with-permissions")
    public RolePermissionsResponseDTO findByIdAndPermissions(@PathVariable Long id) {
        return roleService.findByIdAndPermissions(id);
    }

    @PutMapping("/{id}")
    public RoleResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequestDTO dto) {
        return roleService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }

    // Un rol inactivo no concede permisos a los usuarios que lo tienen.
    @PatchMapping("/{id}/deactivate")
    public RoleResponseDTO deactivate(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return roleService.deactivate(id, Long.valueOf(jwt.getSubject()));
    }

    @PatchMapping("/{id}/activate")
    public RoleResponseDTO activate(@PathVariable Long id) {
        return roleService.activate(id);
    }
}
