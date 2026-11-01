package com.uc.ms_security.controller;

import com.uc.ms_security.dto.auth.RegisterRequestDTO;
import com.uc.ms_security.dto.user.UserResponseDTO;
import com.uc.ms_security.service.IRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// Endpoint público: no requiere token (ver SecurityConfig).
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final IRegistrationService registrationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@Valid @RequestBody RegisterRequestDTO dto) {
        return registrationService.register(dto);
    }
}
