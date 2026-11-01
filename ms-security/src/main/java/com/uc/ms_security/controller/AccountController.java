package com.uc.ms_security.controller;

import com.uc.ms_security.dto.common.MessageResponseDTO;
import com.uc.ms_security.dto.auth.ChangePasswordRequestDTO;
import com.uc.ms_security.dto.auth.TwoFactorSettingsRequestDTO;
import com.uc.ms_security.dto.auth.TwoFactorStatusDTO;
import com.uc.ms_security.dto.session.SessionResponseDTO;
import com.uc.ms_security.service.IPasswordChangeService;
import com.uc.ms_security.service.ISessionService;
import com.uc.ms_security.service.ITwoFactorSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Pantalla "Seguridad": cada usuario gestiona la seguridad de su propia
// cuenta. El id se toma del token, nunca de la URL.
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final IPasswordChangeService passwordChangeService;
    private final ITwoFactorSettingsService twoFactorSettingsService;
    private final ISessionService sessionService;

    @PutMapping("/password")
    public MessageResponseDTO changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ChangePasswordRequestDTO dto) {
        passwordChangeService.changePassword(
                currentUserId(jwt),
                dto.getCurrentPassword(),
                dto.getNewPassword()
        );
        return new MessageResponseDTO(
                "Contraseña actualizada. Por seguridad se cerraron sus sesiones: inicie sesión nuevamente"
        );
    }

    @GetMapping("/2fa")
    public TwoFactorStatusDTO twoFactorStatus(@AuthenticationPrincipal Jwt jwt) {
        return twoFactorSettingsService.getStatus(currentUserId(jwt));
    }

    @PutMapping("/2fa")
    public TwoFactorStatusDTO setTwoFactor(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TwoFactorSettingsRequestDTO dto) {
        return twoFactorSettingsService.setEnabled(
                currentUserId(jwt),
                dto.getEnabled(),
                dto.getCurrentPassword()
        );
    }

    @GetMapping("/sessions")
    public List<SessionResponseDTO> mySessions(@AuthenticationPrincipal Jwt jwt) {
        return sessionService.findMine(currentUserId(jwt), jwt.getId());
    }

    private Long currentUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
