package com.uc.ms_security.service.impl;

import com.uc.ms_security.dto.auth.RegisterRequestDTO;
import com.uc.ms_security.dto.user.UserResponseDTO;
import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.mapper.UserMapper;
import com.uc.ms_security.repository.UserRepository;
import com.uc.ms_security.service.IRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

// Registro manual con credenciales. Las cuentas OAuth no pasan por aquí:
// se crean automáticamente al iniciar sesión con el proveedor (HU-SE-08).
// El usuario registrado queda activo y sin roles; un administrador
// le asigna roles según corresponda (HU-SE-04).
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements IRegistrationService {

    private static final Map<AuthProvider, String> PROVIDER_NAMES = Map.of(
            AuthProvider.GOOGLE, "Google",
            AuthProvider.GITHUB, "GitHub",
            AuthProvider.FACEBOOK, "Facebook"
    );

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO register(RegisterRequestDTO dto) {
        userRepository.findByEmail(dto.getEmail().trim())
                .ifPresent(this::rejectExistingEmail);

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user = userMapper.toEntity(dto, encodedPassword);
        user.setEmail(user.getEmail().trim());
        user.setAuthProvider(AuthProvider.LOCAL);
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    private void rejectExistingEmail(User existing) {
        if (existing.getAuthProvider() != AuthProvider.LOCAL) {
            throw new ApplicationException(
                    ErrorCase.ALREADY_EXISTS,
                    "Este correo ya está registrado mediante "
                            + PROVIDER_NAMES.get(existing.getAuthProvider())
                            + ". Inicie sesión con ese proveedor"
            );
        }
        throw new ApplicationException(
                ErrorCase.ALREADY_EXISTS,
                "El correo electrónico ya se encuentra registrado"
        );
    }
}
