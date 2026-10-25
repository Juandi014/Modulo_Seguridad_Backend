package com.uc.ms_security.service.impl;

import com.uc.ms_security.dto.auth.TwoFactorStatusDTO;
import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.repository.UserRepository;
import com.uc.ms_security.service.ITwoFactorSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Reglas: solo cuentas LOCAL y confirmando la contraseña actual
// (así quien tome una sesión abierta no puede quitar el 2FA).
@Service
@RequiredArgsConstructor
public class TwoFactorSettingsServiceImpl implements ITwoFactorSettingsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TwoFactorStatusDTO getStatus(Long userId) {
        User user = findUser(userId);
        return new TwoFactorStatusDTO(
                user.isTwoFactorEnabled(),
                user.getAuthProvider() == AuthProvider.LOCAL
        );
    }

    @Override
    public TwoFactorStatusDTO setEnabled(Long userId, boolean enabled, String currentPassword) {
        User user = findUser(userId);

        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new ApplicationException(
                    ErrorCase.INVALID_OPERATION,
                    "La autenticación de dos factores solo aplica a cuentas con correo y contraseña. "
                            + "Su cuenta usa la seguridad de su proveedor externo"
            );
        }
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ApplicationException(
                    ErrorCase.INVALID_OPERATION,
                    "La contraseña actual no es correcta"
            );
        }

        user.setTwoFactorEnabled(enabled);
        userRepository.save(user);
        return new TwoFactorStatusDTO(enabled, true);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Usuario no encontrado"
                ));
    }
}
