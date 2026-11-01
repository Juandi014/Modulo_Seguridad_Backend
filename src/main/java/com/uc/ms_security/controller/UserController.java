package com.uc.ms_security.controller;

import com.uc.ms_security.authorization.AuthenticatedUserId;
import com.uc.ms_security.dto.user.CreateUserDTO;
import com.uc.ms_security.dto.user.UpdateUserDTO;
import com.uc.ms_security.dto.user.UserDetailResponseDTO;
import com.uc.ms_security.dto.user.UserResponseDTO;
import com.uc.ms_security.dto.user.UserRolesResponseDTO;
import com.uc.ms_security.dto.user.UserSessionsResponseDTO;
import com.uc.ms_security.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(@Valid @RequestBody CreateUserDTO dto) {
        return userService.create(dto);
    }

    @GetMapping
    public List<UserResponseDTO> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponseDTO findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/{id}/profile")
    public UserDetailResponseDTO findByIdAndProfile(@PathVariable Long id) {
        return userService.findByIdWithProfile(id);
    }

    @GetMapping("/{id}/detail-with-sessions")
    public UserSessionsResponseDTO findByIdAndSessions(@PathVariable Long id) {
        return userService.findByIdAndSessions(id);
    }

    @GetMapping("/{id}/detail-with-roles")
    public UserRolesResponseDTO findByIdAndRoles(@PathVariable Long id) {
        return userService.findByIdAndRoles(id);
    }

    @PutMapping("/{id}")
    public UserResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO dto) {
        return userService.update(id, dto);
    }

    // Los usuarios no se eliminan: se desactivan (campo active).
    @PatchMapping("/{id}/deactivate")
    public UserResponseDTO deactivate(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return userService.deactivate(id, AuthenticatedUserId.from(jwt));
    }

    @PatchMapping("/{id}/activate")
    public UserResponseDTO activate(@PathVariable Long id) {
        return userService.activate(id);
    }
}
