package com.uc.ms_security.mapper;

import com.uc.ms_security.dto.session.SessionResponseDTO;
import com.uc.ms_security.entity.Session;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SessionMapper {

    public SessionResponseDTO toResponseDTO(Session session) {
        return toResponseDTO(session, null);
    }

    public SessionResponseDTO toResponseDTO(Session session, String currentTokenId) {
        boolean active = session.getRevokedAt() == null
                && session.getExpiration().isAfter(LocalDateTime.now());
        return new SessionResponseDTO(
                session.getId(),
                session.getAuthMethod(),
                session.getCreatedAt(),
                session.getExpiration(),
                session.getRevokedAt(),
                active,
                session.getToken().equals(currentTokenId)
        );
    }

    public List<SessionResponseDTO> toResponseDTOList(
            List<Session> sessions, String currentTokenId) {

        return sessions.stream()
                .map(session -> toResponseDTO(session, currentTokenId))
                .toList();
    }
}
