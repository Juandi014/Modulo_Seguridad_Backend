package com.uc.ms_security.service.impl;

import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.repository.UserRepository;
import com.uc.ms_security.service.IPasswordChangeService;
import com.uc.ms_security.service.ISessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordChangeServiceImpl implements IPasswordChangeService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ISessionService sessionService;

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Usuario no encontrado"
                ));

        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new ApplicationException(
                    ErrorCase.INVALID_OPERATION,
                    "Su cuenta inicia sesión con un proveedor externo y no tiene contraseña en la plataforma"
            );
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ApplicationException(
                    ErrorCase.INVALID_OPERATION,
                    "La contraseña actual no es correcta"
            );
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ApplicationException(
                    ErrorCase.INVALID_OPERATION,
                    "La nueva contraseña debe ser diferente de la actual"
            );
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Se cierran todas las sesiones, incluida la actual
        sessionService.closeAllForUser(userId);
    }
}
