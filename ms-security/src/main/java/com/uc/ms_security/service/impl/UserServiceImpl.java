package com.uc.ms_security.service.impl;

import com.uc.ms_security.dto.user.CreateUserDTO;
import com.uc.ms_security.dto.user.UpdateUserDTO;
import com.uc.ms_security.dto.user.UserDetailResponseDTO;
import com.uc.ms_security.dto.user.UserResponseDTO;
import com.uc.ms_security.dto.user.UserRolesResponseDTO;
import com.uc.ms_security.dto.user.UserSessionsResponseDTO;
import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.mapper.UserMapper;
import com.uc.ms_security.repository.UserRepository;
import com.uc.ms_security.service.IUserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO create(CreateUserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ApplicationException(
                ErrorCase.ALREADY_EXISTS,
                    "Ya existe un usuario con este email"
            );
        }
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user = userMapper.toEntity(dto, encodedPassword);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    public List<UserResponseDTO> findAll() {
        List<User> users =userRepository.findAll();
        return userMapper.toResponseDTOList(users);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ApplicationException(
                ErrorCase.NOT_FOUND,
                "Usuario no encontrado con id: " + id
                ));
    }

    @Override
    public UserResponseDTO findById(Long id) {
        User user = findUser(id);
        return userMapper.toResponseDTO(user);
    }

    @Override
    public UserDetailResponseDTO findByIdWithProfile(Long id) {
        User user = userRepository.findWithProfileById(id)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Usuario no encontrado con id: " + id
                ));
        return userMapper.toDetailResponseDTO(user);
    }

    @Override
    public UserSessionsResponseDTO findByIdAndSessions(Long id) {
        User user = userRepository.findWithSessionsById(id)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Usuario no encontrado con id: " + id
                ));

        return userMapper.toSessionsResponseDTO(user);
    }

    @Override
    public UserRolesResponseDTO findByIdAndRoles(Long id) {
        User user = userRepository.findWithRolesById(id)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Usuario no encontrado con id: " + id
                ));

        return userMapper.toRolesResponseDTO(user);
    }

    @Override
    public UserResponseDTO update(Long id, UpdateUserDTO dto) {
        User user = findUser(id);
        if (userRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new ApplicationException(
                ErrorCase.ALREADY_EXISTS,
                    "El email pertenece a otro usuario"
            );
        }
        boolean changesPassword = dto.getPassword() != null && !dto.getPassword().isBlank();
        if (changesPassword && user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new ApplicationException(
                ErrorCase.INVALID_OPERATION,
                    "Los usuarios que ingresan con un proveedor externo no tienen contraseña en la plataforma"
            );
        }
        userMapper.updateBasicData(dto, user);

        if (changesPassword) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }

    // Desactivar solo cambia el estado: el usuario conserva sus roles,
    // perfil y sesiones, y los roles y permisos del sistema no se tocan.
    @Override
    public UserResponseDTO deactivate(Long id, Long currentUserId) {
        User user = findUser(id);
        if (id.equals(currentUserId)) {
            throw new ApplicationException(
                ErrorCase.INVALID_OPERATION,
                    "No puede desactivar su propio usuario"
            );
        }
        if (!user.isActive()) {
            throw new ApplicationException(
                ErrorCase.CONFLICT,
                    "El usuario ya se encuentra desactivado"
            );
        }
        user.setActive(false);
        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO activate(Long id) {
        User user = findUser(id);
        if (user.isActive()) {
            throw new ApplicationException(
                ErrorCase.CONFLICT,
                    "El usuario ya se encuentra activo"
            );
        }
        user.setActive(true);
        return userMapper.toResponseDTO(userRepository.save(user));
    }
}
