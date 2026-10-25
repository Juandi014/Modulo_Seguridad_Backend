package com.uc.ms_security.service.impl;

import com.uc.ms_security.authentication.oauth.IOAuthProviderRegistry;
import com.uc.ms_security.dto.auth.OAuthProfileDTO;
import com.uc.ms_security.dto.AuthResponseDTO;
import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.enums.LoginAttemptResult;
import com.uc.ms_security.service.ILoginAuditService;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.repository.UserRepository;
import com.uc.ms_security.service.IOAuthExchangeCodeStore;
import com.uc.ms_security.service.IOAuthLoginService;
import com.uc.ms_security.service.ISessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// La identidad ya fue validada por el proveedor: aquí no hay contraseña
// ni 2FA interno. Solo se identifica (o crea) el usuario y se abre la sesión.
@Service
@RequiredArgsConstructor
public class OAuthLoginServiceImpl implements IOAuthLoginService {

    private final UserRepository userRepository;
    private final ISessionService sessionService;
    private final IOAuthExchangeCodeStore exchangeCodeStore;
    private final IOAuthProviderRegistry providerRegistry;
    private final ILoginAuditService loginAuditService;

    // Cada salida queda en la auditoría (el registro usa su propia transacción,
    // por eso se conserva aunque esta se deshaga por el rechazo)
    @Override
    @Transactional
    public AuthResponseDTO login(OAuthProfileDTO profile) {
        if (profile.providerId() == null) {
            audit(profile, null, LoginAttemptResult.OAUTH_REJECTED);
            throw new ApplicationException(
                    ErrorCase.UNAUTHORIZED,
                    "No fue posible identificar la cuenta de " + providerName(profile.provider())
            );
        }

        // Accesos posteriores: mismo proveedor + mismo id = mismo usuario
        User user;
        try {
            user = userRepository
                    .findByAuthProviderAndProviderId(profile.provider(), profile.providerId())
                    .orElseGet(() -> createUser(profile));
        } catch (ApplicationException rejected) {
            audit(profile, null, LoginAttemptResult.OAUTH_REJECTED);
            throw rejected;
        }

        if (!user.isActive()) {
            audit(profile, user, LoginAttemptResult.USER_INACTIVE);
            throw new ApplicationException(
                    ErrorCase.UNAUTHORIZED,
                    "El usuario está desactivado"
            );
        }

        AuthResponseDTO session = sessionService.open(user, profile.provider());
        audit(profile, user, LoginAttemptResult.SUCCESS);
        return session;
    }

    private void audit(OAuthProfileDTO profile, User user, LoginAttemptResult result) {
        loginAuditService.record(profile.email(), user, profile.provider(), result);
    }

    @Override
    public AuthResponseDTO exchange(String code) {
        return exchangeCodeStore.consume(code)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.UNAUTHORIZED,
                        "El código de inicio de sesión es inválido o expiró. Intente de nuevo"
                ));
    }

    @Override
    public List<String> enabledProviders() {
        return providerRegistry.enabledProviders();
    }

    // Primer acceso: se crea el usuario con los datos del proveedor
    private User createUser(OAuthProfileDTO profile) {
        String email = profile.email();
        if (email == null || email.isBlank()) {
            throw new ApplicationException(
                    ErrorCase.UNAUTHORIZED,
                    providerName(profile.provider())
                            + " no compartió un correo electrónico verificado de su cuenta"
            );
        }

        // Evitar duplicados: el correo no puede pertenecer a otra cuenta
        userRepository.findByEmail(email.trim()).ifPresent(existing -> {
            if (existing.getAuthProvider() == AuthProvider.LOCAL) {
                throw new ApplicationException(
                        ErrorCase.CONFLICT,
                        "Este correo ya está registrado con correo y contraseña. "
                                + "Inicie sesión con sus credenciales"
                );
            }
            throw new ApplicationException(
                    ErrorCase.CONFLICT,
                    "Este correo ya está asociado a una cuenta de "
                            + providerName(existing.getAuthProvider())
                            + ". Inicie sesión con ese proveedor"
            );
        });

        User user = new User();
        user.setName(resolveName(profile));
        user.setEmail(email.trim());
        user.setPassword(null);
        user.setAuthProvider(profile.provider());
        user.setProviderId(profile.providerId());
        return userRepository.save(user);
    }

    private String resolveName(OAuthProfileDTO profile) {
        String name = profile.name();
        if (name == null || name.isBlank()) {
            name = profile.email().substring(0, profile.email().indexOf('@'));
        }
        return name.length() > 100 ? name.substring(0, 100) : name;
    }

    private String providerName(AuthProvider provider) {
        return switch (provider) {
            case GOOGLE -> "Google";
            case GITHUB -> "GitHub";
            case FACEBOOK -> "Facebook";
            case LOCAL -> "la plataforma";
        };
    }
}
