package com.uc.ms_security.service.impl;

import com.uc.ms_security.authentication.SecureTokenGenerator;
import com.uc.ms_security.dto.auth.AuthResponseDTO;
import com.uc.ms_security.dto.auth.IssuedTokenDTO;
import com.uc.ms_security.dto.session.SessionResponseDTO;
import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.Session;
import com.uc.ms_security.entity.enums.SessionStatus;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.mapper.SessionMapper;
import com.uc.ms_security.mapper.UserMapper;
import com.uc.ms_security.repository.SessionRepository;
import com.uc.ms_security.repository.UserRepository;
import com.uc.ms_security.service.IAccessTokenIssuer;
import com.uc.ms_security.service.ISessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements ISessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final IAccessTokenIssuer accessTokenIssuer;
    private final SessionMapper sessionMapper;
    private final UserMapper userMapper;
    private final SecureTokenGenerator tokenGenerator;

    @Override
    public AuthResponseDTO open(User user, AuthProvider authMethod) {
        Session session = new Session();
        session.setUser(user);
        session.setCreatedAt(LocalDateTime.now());
        session.setAuthMethod(authMethod);
        return issueToken(session);
    }

    @Override
    public Session openPending(User user, String codeHash, LocalDateTime expiresAt) {
        Session pending = new Session();
        pending.setUser(user);
        pending.setToken(tokenGenerator.randomToken()); // identificador del intento
        pending.setStatus(SessionStatus.PENDING_2FA);
        pending.setAuthMethod(AuthProvider.LOCAL);
        pending.setCreatedAt(LocalDateTime.now());
        pending.setExpiration(expiresAt);
        pending.setTwoFactorCodeHash(codeHash);
        pending.setTwoFactorAttempts(0);
        return sessionRepository.save(pending);
    }

    @Override
    public Optional<Session> findPending(String challengeId) {
        return sessionRepository.findByToken(challengeId)
                .filter(session -> session.getStatus() == SessionStatus.PENDING_2FA)
                .filter(session -> session.getRevokedAt() == null)
                .filter(session -> session.getTwoFactorCodeHash() != null);
    }

    @Override
    public void updatePending(Session pendingSession) {
        sessionRepository.save(pendingSession);
    }

    @Override
    public void invalidate(Session pendingSession) {
        pendingSession.setTwoFactorCodeHash(null);
        pendingSession.setRevokedAt(LocalDateTime.now());
        sessionRepository.save(pendingSession);
    }

    @Override
    public AuthResponseDTO activate(Session pendingSession) {
        pendingSession.setTwoFactorCodeHash(null);
        return issueToken(pendingSession);
    }

    // La sesión queda ACTIVE con el id del token (jti) y su expiración
    private AuthResponseDTO issueToken(Session session) {
        User user = session.getUser();
        IssuedTokenDTO token = accessTokenIssuer.issue(user);

        session.setToken(token.id());
        session.setExpiration(LocalDateTime.ofInstant(token.expiresAt(), ZoneId.systemDefault()));
        session.setStatus(SessionStatus.ACTIVE);
        sessionRepository.save(session);

        return new AuthResponseDTO(
                token.value(),
                "Bearer",
                token.expiresAt(),
                userMapper.toResponseDTO(user)
        );
    }

    @Override
    public boolean isActive(String tokenId) {
        return sessionRepository.existsByTokenAndStatusAndRevokedAtIsNullAndExpirationAfter(
                tokenId, SessionStatus.ACTIVE, LocalDateTime.now());
    }

    @Override
    public void close(String tokenId) {
        sessionRepository.findByToken(tokenId).ifPresent(session -> {
            if (session.getRevokedAt() == null) {
                session.setRevokedAt(LocalDateTime.now());
                sessionRepository.save(session);
            }
        });
    }

    @Override
    public List<SessionResponseDTO> findAllByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ApplicationException(
                    ErrorCase.NOT_FOUND,
                    "Usuario no encontrado con id: " + userId
            );
        }
        return findMine(userId, null);
    }

    @Override
    public List<SessionResponseDTO> findMine(Long userId, String currentTokenId) {
        // Solo sesiones que llegaron a completarse (no los intentos 2FA pendientes)
        return sessionMapper.toResponseDTOList(
                sessionRepository.findAllByUserIdAndStatusOrderByCreatedAtDesc(userId, SessionStatus.ACTIVE),
                currentTokenId
        );
    }

    @Override
    public void closeAllForUser(Long userId) {
        List<Session> openSessions = sessionRepository.findAllByUserIdAndRevokedAtIsNull(userId);
        LocalDateTime now = LocalDateTime.now();
        openSessions.forEach(session -> session.setRevokedAt(now));
        sessionRepository.saveAll(openSessions);
    }

    @Override
    public SessionResponseDTO revoke(Long userId, Long sessionId) {
        Session session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Sesión no encontrada para este usuario"
                ));
        if (session.getRevokedAt() == null) {
            session.setRevokedAt(LocalDateTime.now());
            sessionRepository.save(session);
        }
        return sessionMapper.toResponseDTO(session);
    }
}
