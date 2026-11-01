package com.uc.ms_security.controller;

import com.uc.ms_security.dto.common.MessageResponseDTO;
import com.uc.ms_security.dto.auth.PasswordRecoveryRequestDTO;
import com.uc.ms_security.dto.auth.PasswordResetRequestDTO;
import com.uc.ms_security.service.IPasswordRecoveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// Endpoints públicos (el usuario no puede iniciar sesión: olvidó su contraseña).
@RestController
@RequestMapping("/api/auth/password-recovery")
@RequiredArgsConstructor
public class PasswordRecoveryController {

    private final IPasswordRecoveryService passwordRecoveryService;

    @PostMapping("/request")
    public MessageResponseDTO request(@Valid @RequestBody PasswordRecoveryRequestDTO dto) {
        passwordRecoveryService.requestRecovery(dto.getEmail());
        return new MessageResponseDTO(
                "Si el correo está registrado, recibirá un mensaje con las instrucciones para recuperar el acceso"
        );
    }

    @GetMapping("/validate")
    public MessageResponseDTO validate(@RequestParam String token) {
        passwordRecoveryService.validateToken(token);
        return new MessageResponseDTO("Enlace válido");
    }

    @PostMapping("/reset")
    public MessageResponseDTO reset(@Valid @RequestBody PasswordResetRequestDTO dto) {
        passwordRecoveryService.resetPassword(dto.getToken(), dto.getNewPassword());
        return new MessageResponseDTO(
                "Su contraseña fue actualizada. Ya puede iniciar sesión con la nueva contraseña"
        );
    }
}
