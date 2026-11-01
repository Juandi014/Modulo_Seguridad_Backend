package com.uc.ms_security.controller;

import com.uc.ms_security.dto.auth.AuthResponseDTO;
import com.uc.ms_security.dto.auth.LoginRequestDTO;
import com.uc.ms_security.dto.auth.LoginResponseDTO;
import com.uc.ms_security.dto.auth.OAuthExchangeRequestDTO;
import com.uc.ms_security.dto.auth.TwoFactorCancelRequestDTO;
import com.uc.ms_security.dto.auth.TwoFactorVerifyRequestDTO;
import com.uc.ms_security.service.IAuthService;
import com.uc.ms_security.service.IOAuthLoginService;
import com.uc.ms_security.service.ITwoFactorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// El inicio de sesión OAuth comienza en /oauth2/authorization/{proveedor}
// (lo atiende Spring Security) y termina en /api/auth/oauth/exchange.
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private final IOAuthLoginService oauthLoginService;
    private final ITwoFactorService twoFactorService;

    @PostMapping("/login")
    public LoginResponseDTO login(
            @Valid @RequestBody LoginRequestDTO dto) {

        return authService.login(dto);
    }

    // Segundo paso del login cuando el usuario tiene 2FA habilitado
    @PostMapping("/2fa/verify")
    public AuthResponseDTO verifyTwoFactor(
            @Valid @RequestBody TwoFactorVerifyRequestDTO dto) {
        return twoFactorService.verify(dto.getChallengeId(), dto.getCode());
    }

    @PostMapping("/2fa/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelTwoFactor(
            @Valid @RequestBody TwoFactorCancelRequestDTO dto) {
        twoFactorService.cancel(dto.getChallengeId());
    }

    // Cierra la sesión del token con el que se hace la petición
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal Jwt jwt) {
        authService.logout(jwt.getId());
    }

    @GetMapping("/oauth/providers")
    public List<String> oauthProviders() {
        return oauthLoginService.enabledProviders();
    }

    @PostMapping("/oauth/exchange")
    public AuthResponseDTO oauthExchange(
            @Valid @RequestBody OAuthExchangeRequestDTO dto) {
        return oauthLoginService.exchange(dto.getCode());
    }
}
