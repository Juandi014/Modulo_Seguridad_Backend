package com.uc.ms_security.service.impl;

import com.uc.ms_security.authentication.SecureTokenGenerator;
import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.PasswordResetToken;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.repository.PasswordResetTokenRepository;
import com.uc.ms_security.repository.UserRepository;
import com.uc.ms_security.service.IEmailService;
import com.uc.ms_security.service.IPasswordRecoveryService;
import com.uc.ms_security.service.ISessionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PasswordRecoveryServiceImpl implements IPasswordRecoveryService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final SecureTokenGenerator tokenGenerator;
    private final IEmailService emailService;
    private final ISessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    private final String frontendUrl;
    private final long durationMinutes;

    public PasswordRecoveryServiceImpl(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            SecureTokenGenerator tokenGenerator,
            IEmailService emailService,
            ISessionService sessionService,
            PasswordEncoder passwordEncoder,
            @Value("${security.frontend-url}") String frontendUrl,
            @Value("${security.password-recovery.duration-minutes}") long durationMinutes) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.tokenGenerator = tokenGenerator;
        this.emailService = emailService;
        this.sessionService = sessionService;
        this.passwordEncoder = passwordEncoder;
        this.frontendUrl = frontendUrl;
        this.durationMinutes = durationMinutes;
    }

    @Override
    @Transactional
    public void requestRecovery(String email) {
        userRepository.findByEmail(email.trim()).ifPresent(user -> {
            if (!user.isActive()) {
                return;
            }
            if (user.getAuthProvider() != AuthProvider.LOCAL) {
                sendUseYourProviderEmail(user);
                return;
            }
            sendRecoveryLink(user);
        });
        // Si el correo no existe no se hace nada: la respuesta es la misma
    }

    @Override
    public void validateToken(String token) {
        findUsableToken(token);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = findUsableToken(token);
        User user = resetToken.getUser();
        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw invalidLink();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Un solo uso: queda marcado y no puede volver a utilizarse
        resetToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);

        // Quien tuviera la contraseña anterior pierde el acceso
        sessionService.closeAllForUser(user.getId());
    }

    private void sendRecoveryLink(User user) {
        // Una solicitud nueva anula las anteriores que no se usaron
        LocalDateTime now = LocalDateTime.now();
        List<PasswordResetToken> pending = tokenRepository.findAllByUserIdAndUsedAtIsNull(user.getId());
        pending.forEach(previous -> previous.setUsedAt(now));
        tokenRepository.saveAll(pending);

        String token = tokenGenerator.randomToken();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(tokenGenerator.sha256(token));
        resetToken.setCreatedAt(now);
        resetToken.setExpiresAt(now.plusMinutes(durationMinutes));
        tokenRepository.save(resetToken);

        String link = frontendUrl + "/#/reset-password?token=" + token;
        emailService.send(
                user.getEmail(),
                "SportManager - Recuperación de contraseña",
                "Hola " + user.getName() + ",\n\n"
                        + "Recibimos una solicitud para restablecer su contraseña.\n"
                        + "Use este enlace (válido por " + durationMinutes + " minutos y de un solo uso):\n\n"
                        + link + "\n\n"
                        + "Si usted no hizo la solicitud, ignore este correo: su contraseña no cambiará."
        );
    }

    private void sendUseYourProviderEmail(User user) {
        String provider = switch (user.getAuthProvider()) {
            case GOOGLE -> "Google";
            case GITHUB -> "GitHub";
            case FACEBOOK -> "Facebook";
            case LOCAL -> "la plataforma";
        };
        emailService.send(
                user.getEmail(),
                "SportManager - Recuperación de acceso",
                "Hola " + user.getName() + ",\n\n"
                        + "Su cuenta de SportManager inicia sesión con " + provider
                        + " y no tiene una contraseña en la plataforma.\n"
                        + "Si no puede ingresar, recupere el acceso directamente con " + provider + "."
        );
    }

    private PasswordResetToken findUsableToken(String token) {
        PasswordResetToken resetToken = tokenRepository
                .findByTokenHash(tokenGenerator.sha256(token))
                .orElseThrow(this::invalidLink);
        if (resetToken.getUsedAt() != null) {
            throw invalidLink();
        }
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApplicationException(
                    ErrorCase.INVALID_OPERATION,
                    "El enlace de recuperación expiró. Solicite uno nuevo"
            );
        }
        return resetToken;
    }

    private ApplicationException invalidLink() {
        return new ApplicationException(
                ErrorCase.INVALID_OPERATION,
                "El enlace de recuperación no es válido o ya fue utilizado"
        );
    }
}
