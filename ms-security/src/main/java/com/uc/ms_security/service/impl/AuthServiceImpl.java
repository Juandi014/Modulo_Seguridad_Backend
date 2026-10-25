package com.uc.ms_security.service.impl;

import com.uc.ms_security.dto.LoginRequestDTO;
import com.uc.ms_security.dto.auth.LoginResponseDTO;
import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.enums.LoginAttemptResult;
import com.uc.ms_security.service.ILoginAuditService;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.repository.UserRepository;
import com.uc.ms_security.service.IAuthService;
import com.uc.ms_security.service.ISessionService;
import com.uc.ms_security.service.ITwoFactorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Antes AuthService (guía 7). Valida credenciales y abre la sesión interna.
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ISessionService sessionService;
    private final ITwoFactorService twoFactorService;
    private final ILoginAuditService loginAuditService;

    // Cada salida (exitosa o fallida) queda registrada en la auditoría
    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        String email = dto.getEmail().trim();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            audit(email, null, LoginAttemptResult.INVALID_CREDENTIALS);
            throw unauthorized();
        }

        // Las cuentas OAuth no tienen contraseña propia en la plataforma
        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            audit(email, user, LoginAttemptResult.EXTERNAL_ACCOUNT);
            throw new ApplicationException(
                    ErrorCase.UNAUTHORIZED,
                    "Esta cuenta inicia sesión con " + providerName(user.getAuthProvider())
                            + ". Use el botón de ese proveedor"
            );
        }

        boolean validPassword = passwordEncoder.matches(
                dto.getPassword(),
                user.getPassword()
        );

        if (!validPassword) {
            audit(email, user, LoginAttemptResult.INVALID_CREDENTIALS);
            throw unauthorized();
        }

        if (!user.isActive()) {
            audit(email, user, LoginAttemptResult.USER_INACTIVE);
            throw new ApplicationException(
                    ErrorCase.UNAUTHORIZED,
                    "El usuario está desactivado"
            );
        }

        // Con 2FA: la sesión queda pendiente hasta validar el código (sin token)
        if (user.isTwoFactorEnabled()) {
            LoginResponseDTO pending = LoginResponseDTO.pendingTwoFactor(twoFactorService.startChallenge(user));
            audit(email, user, LoginAttemptResult.PENDING_2FA);
            return pending;
        }

        LoginResponseDTO completed = LoginResponseDTO.completed(sessionService.open(user, AuthProvider.LOCAL));
        audit(email, user, LoginAttemptResult.SUCCESS);
        return completed;
    }

    private void audit(String email, User user, LoginAttemptResult result) {
        loginAuditService.record(email, user, AuthProvider.LOCAL, result);
    }

    @Override
    public void logout(String tokenId) {
        sessionService.close(tokenId);
    }

    // Mismo mensaje si el correo no existe o la contraseña es incorrecta
    private ApplicationException unauthorized() {
        return new ApplicationException(
                ErrorCase.UNAUTHORIZED,
                "Credenciales incorrectas"
        );
    }

    private String providerName(AuthProvider provider) {
        return switch (provider) {
            case GOOGLE -> "Google";
            case GITHUB -> "GitHub";
            case FACEBOOK -> "Facebook";
            case LOCAL -> "correo y contraseña";
        };
    }
}
