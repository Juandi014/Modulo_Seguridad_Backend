package com.uc.ms_security.controller;

import com.uc.ms_security.authorization.AuthenticatedUserId;
import com.uc.ms_security.dto.role.RoleResponseDTO;
import com.uc.ms_security.dto.user.UserResponseDTO;
import com.uc.ms_security.dto.userrole.AssignRoleRequestDTO;
import com.uc.ms_security.dto.userrole.RoleUserResponseDTO;
import com.uc.ms_security.dto.userrole.UserRoleResponseDTO;
import com.uc.ms_security.service.IUserRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final IUserRoleService userRoleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserRoleResponseDTO assign(@Valid @RequestBody AssignRoleRequestDTO dto) {
        return userRoleService.assign(dto);
    }

    @GetMapping
    public List<UserRoleResponseDTO> findAll() {
        return userRoleService.findAll();
    }

    @GetMapping("/users/{userId}")
    public List<UserRoleResponseDTO> findByUserId(@PathVariable Long userId) {
        return userRoleService.findByUserId(userId);
    }

    @GetMapping("/users/{userId}/available-roles")
    public List<RoleResponseDTO> findAvailableRoles(@PathVariable Long userId) {
        return userRoleService.findAvailableRoles(userId);
    }

    @GetMapping("/roles/{roleId}")
    public List<RoleUserResponseDTO> findByRoleId(@PathVariable Long roleId) {
        return userRoleService.findByRoleId(roleId);
    }

    @GetMapping("/roles/{roleId}/available-users")
    public List<UserResponseDTO> findAvailableUsers(@PathVariable Long roleId) {
        return userRoleService.findAvailableUsers(roleId);
    }

    @DeleteMapping("/{userId}/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long userId,
            @PathVariable Long roleId,
            @AuthenticationPrincipal Jwt jwt) {
        userRoleService.delete(userId, roleId, AuthenticatedUserId.from(jwt));
    }
}
