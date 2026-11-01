package com.uc.ms_security.repository;

import com.uc.ms_security.entity.Session;
import com.uc.ms_security.entity.enums.SessionStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findAllByUserIdAndStatusOrderByCreatedAtDesc(Long userId, SessionStatus status);

    Optional<Session> findByIdAndUserId(Long sessionId, Long userId);

    @EntityGraph(attributePaths = {"user"})
    Optional<Session> findByToken(String token);

    List<Session> findAllByUserIdAndRevokedAtIsNull(Long userId);

    // Sesión vigente: activa (2FA completo si aplica), sin cerrar y sin expirar
    boolean existsByTokenAndStatusAndRevokedAtIsNullAndExpirationAfter(
            String token,
            SessionStatus status,
            LocalDateTime now
    );
}
