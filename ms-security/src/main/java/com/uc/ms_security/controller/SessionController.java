package com.uc.ms_security.controller;

import com.uc.ms_security.dto.session.SessionResponseDTO;
import com.uc.ms_security.service.ISessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Consulta y cierre de sesiones de un usuario (administración).
// Las sesiones ya no se crean ni editan a mano: solo nacen de un login.
@RestController
@RequestMapping("/api/users/{userId}/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final ISessionService sessionService;

    @GetMapping
    public List<SessionResponseDTO> findAll(@PathVariable Long userId) {
        return sessionService.findAllByUserId(userId);
    }

    @PatchMapping("/{sessionId}/revoke")
    public SessionResponseDTO revoke(
            @PathVariable Long userId,
            @PathVariable Long sessionId) {
        return sessionService.revoke(userId, sessionId);
    }
}
