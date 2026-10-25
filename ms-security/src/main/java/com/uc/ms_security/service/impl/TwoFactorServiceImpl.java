package com.uc.ms_security.service.impl;

import com.uc.ms_security.authentication.SecureTokenGenerator;
import com.uc.ms_security.dto.AuthResponseDTO;
import com.uc.ms_security.dto.auth.TwoFactorChallengeDTO;
import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.enums.LoginAttemptResult;
import com.uc.ms_security.entity.Session;
import com.uc.ms_security.entity.enums.SessionStatus;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.repository.SessionRepository;
import com.uc.ms_security.service.IEmailService;
import com.uc.ms_security.service.ILoginAuditService;
import com.uc.ms_security.service.ISessionService;
import com.uc.ms_security.service.ITwoFactorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;

// El código está asociado a la sesión que se está iniciando (tabla sessions,
// estado PENDING_2FA). Vence en 5 minutos, es de un solo uso y admite un
// número limitado de intentos.
@Service
public class TwoFactorServiceImpl implements ITwoFactorService {

    private final SessionRepository sessionRepository;
    private final ISessionService sessionService;
    private final IEmailService emailService;
    private final SecureTokenGenerator tokenGenerator;
    private final ILoginAuditService loginAuditService;
    private final long codeMinutes;
    private final int maxAttempts;

    public TwoFactorServiceImpl(
            SessionRepository sessionRepository,
            ISessionService sessionService,
            IEmailService emailService,
            SecureTokenGenerator tokenGenerator,
            ILoginAuditService loginAuditService,
            @Value("${security.two-factor.code-minutes}") long codeMinutes,
            @Value("${security.two-factor.max-attempts}") int maxAttempts) {
        this.sessionRepository = sessionRepository;
        this.sessionService = sessionService;
        this.emailService = emailService;
        this.tokenGenerator = tokenGenerator;
        this.loginAuditService = loginAuditService;
        this.codeMinutes = codeMinutes;
        this.maxAttempts = maxAttempts;
    }

    @Override
    public TwoFactorChallengeDTO startChallenge(User user) {
        String code = tokenGenerator.randomDigits(6);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(codeMinutes);

        Session pending = new Session();
        pending.setUser(user);
        pending.setToken(tokenGenerator.randomToken()); // identificador del intento
        pending.setStatus(SessionStatus.PENDING_2FA);
        pending.setAuthMethod(AuthProvider.LOCAL);
        pending.setCreatedAt(now);
        pending.setExpiration(expiresAt);
        pending.setTwoFactorCodeHash(tokenGenerator.sha256(code));
        pending.setTwoFactorAttempts(0);
        sessionRepository.save(pending);

        emailService.send(
                user.getEmail(),
                "SportManager - Código de verificación",
                "Hola " + user.getName() + ",\n\n"
                        + "Su código de verificación es: " + code + "\n\n"
                        + "Es válido por " + codeMinutes + " minutos y solo sirve para este inicio de sesión.\n"
                        + "Si usted no intentó ingresar, cambie su contraseña."
        );

        return new TwoFactorChallengeDTO(
                pending.getToken(),
                maskEmail(user.getEmail()),
                expiresAt.atZone(ZoneId.systemDefault()).toInstant()
        );
    }

    // Sin @Transactional a propósito: un intento fallido debe quedar guardado
    // aunque la respuesta sea un error.
    @Override
    public AuthResponseDTO verify(String challengeId, String code) {
        Session pending = findPending(challengeId);

        if (pending.getExpiration().isBefore(LocalDateTime.now())) {
            invalidate(pending);
            audit(pending, LoginAttemptResult.EXPIRED_2FA_CODE);
            throw new ApplicationException(
                    ErrorCase.UNAUTHORIZED,
                    "El código expiró. Inicie sesión nuevamente para recibir uno nuevo"
            );
        }

        if (!matches(code, pending.getTwoFactorCodeHash())) {
            int attempts = pending.getTwoFactorAttempts() + 1;
            pending.setTwoFactorAttempts(attempts);
            if (attempts >= maxAttempts) {
                invalidate(pending);
                audit(pending, LoginAttemptResult.TOO_MANY_2FA_ATTEMPTS);
                throw new ApplicationException(
                        ErrorCase.UNAUTHORIZED,
                        "Demasiados intentos fallidos. Inicie sesión nuevamente"
                );
            }
            sessionRepository.save(pending);
            audit(pending, LoginAttemptResult.INVALID_2FA_CODE);
            throw new ApplicationException(
                    ErrorCase.UNAUTHORIZED,
                    "Código incorrecto. Le quedan " + (maxAttempts - attempts) + " intento(s)"
            );
        }

        if (!pending.getUser().isActive()) {
            invalidate(pending);
            audit(pending, LoginAttemptResult.USER_INACTIVE);
            throw new ApplicationException(ErrorCase.UNAUTHORIZED, "El usuario está desactivado");
        }

        // Código correcto: se borra (un solo uso) y la sesión pasa a ACTIVE con su token
        AuthResponseDTO session = sessionService.activate(pending);
        audit(pending, LoginAttemptResult.SUCCESS);
        return session;
    }

    private void audit(Session pending, LoginAttemptResult result) {
        User user = pending.getUser();
        loginAuditService.record(user.getEmail(), user, AuthProvider.LOCAL, result);
    }

    @Override
    public void cancel(String challengeId) {
        sessionRepository.findByToken(challengeId)
                .filter(session -> session.getStatus() == SessionStatus.PENDING_2FA)
                .ifPresent(this::invalidate);
    }

    private Session findPending(String challengeId) {
        return sessionRepository.findByToken(challengeId)
                .filter(session -> session.getStatus() == SessionStatus.PENDING_2FA)
                .filter(session -> session.getRevokedAt() == null)
                .filter(session -> session.getTwoFactorCodeHash() != null)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.UNAUTHORIZED,
                        "El intento de inicio de sesión no es válido. Inicie sesión nuevamente"
                ));
    }

    private void invalidate(Session pending) {
        pending.setTwoFactorCodeHash(null);
        pending.setRevokedAt(LocalDateTime.now());
        sessionRepository.save(pending);
    }

    // Comparación en tiempo constante
    private boolean matches(String code, String expectedHash) {
        return MessageDigest.isEqual(
                tokenGenerator.sha256(code).getBytes(StandardCharsets.UTF_8),
                expectedHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    // juan.perez@uc.edu.co -> j*******@uc.edu.co
    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) {
            return email;
        }
        return email.charAt(0) + "*".repeat(at - 1) + email.substring(at);
    }
}
