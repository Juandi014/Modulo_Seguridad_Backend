package com.uc.ms_security.service;

import com.uc.ms_security.dto.auth.AuthResponseDTO;
import com.uc.ms_security.dto.session.SessionResponseDTO;
import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.Session;
import com.uc.ms_security.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Ciclo de vida de la sesión interna, común a todos los mecanismos de login.
public interface ISessionService {

    /** Crea la sesión del usuario ya autenticado y emite su token. */
    AuthResponseDTO open(User user, AuthProvider authMethod);

    /**
     * Crea la sesión de un inicio de sesión que espera el 2FA (estado PENDING_2FA).
     * Su token es solo el identificador del intento, no un JWT.
     */
    Session openPending(User user, String codeHash, LocalDateTime expiresAt);

    /** Sesión pendiente de 2FA aún utilizable (no revocada y con código vigente). */
    Optional<Session> findPending(String challengeId);

    /** Guarda los cambios de una sesión pendiente (ej. el conteo de intentos). */
    void updatePending(Session pendingSession);

    /** Anula una sesión pendiente: borra su código y la marca como revocada. */
    void invalidate(Session pendingSession);

    /** Activa una sesión que esperaba el 2FA: le emite su token y su expiración. */
    AuthResponseDTO activate(Session pendingSession);

    /** Indica si el token pertenece a una sesión activa, no revocada y no expirada. */
    boolean isActive(String tokenId);

    /** Cierra la sesión del token indicado (logout): el token deja de ser válido. */
    void close(String tokenId);

    List<SessionResponseDTO> findAllByUserId(Long userId);

    /** Sesiones del propio usuario, marcando la sesión actual. */
    List<SessionResponseDTO> findMine(Long userId, String currentTokenId);

    /** Cierra una sesión específica de un usuario. */
    SessionResponseDTO revoke(Long userId, Long sessionId);

    /** Cierra todas las sesiones abiertas del usuario (ej. tras cambiar la contraseña). */
    void closeAllForUser(Long userId);
}
