package com.uc.ms_security.controller;

import com.uc.ms_security.dto.common.PageResponseDTO;
import com.uc.ms_security.dto.audit.LoginAttemptResponseDTO;
import com.uc.ms_security.entity.enums.LoginAttemptResult;
import com.uc.ms_security.service.ILoginAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// Auditoría: solo lectura (no hay endpoints para crear, editar ni borrar).
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final ILoginAuditService loginAuditService;

    @GetMapping("/login-attempts")
    public PageResponseDTO<LoginAttemptResponseDTO> loginAttempts(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) LoginAttemptResult result,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return loginAuditService.search(email, result, page, size);
    }
}
