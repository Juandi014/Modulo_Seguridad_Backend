package com.uc.ms_security.service.impl;

import com.uc.ms_security.dto.role.RoleResponseDTO;
import com.uc.ms_security.dto.user.UserResponseDTO;
import com.uc.ms_security.dto.userrole.AssignRoleRequestDTO;
import com.uc.ms_security.dto.userrole.RoleUserResponseDTO;
import com.uc.ms_security.dto.userrole.UserRoleResponseDTO;
import com.uc.ms_security.entity.Role;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.entity.UserRole;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.mapper.RoleMapper;
import com.uc.ms_security.mapper.UserMapper;
import com.uc.ms_security.mapper.UserRoleMapper;
import com.uc.ms_security.repository.RoleRepository;
import com.uc.ms_security.repository.UserRepository;
import com.uc.ms_security.repository.UserRoleRepository;
import com.uc.ms_security.service.IUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Asignación de roles a usuarios (relación N:N a través de UserRole).
// Los permisos efectivos se calculan en cada petición a partir de estas
// asignaciones, por lo que cualquier cambio se refleja de inmediato.
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements IUserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRoleMapper userRoleMapper;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public UserRoleResponseDTO assign(AssignRoleRequestDTO dto) {
        User user = findUser(dto.getUserId());
        Role role = findRole(dto.getRoleId());

        if (userRoleRepository.existsByUserIdAndRoleId(dto.getUserId(), dto.getRoleId())) {
            throw new ApplicationException(
                    ErrorCase.ALREADY_EXISTS,
                    "El usuario ya tiene asignado ese rol"
            );
        }

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        return userRoleMapper.toResponseDTO(userRoleRepository.save(userRole));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleResponseDTO> findAll() {
        return userRoleMapper.toResponseDTOList(userRoleRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleResponseDTO> findByUserId(Long userId) {
        findUser(userId);
        return userRoleMapper.toResponseDTOList(userRoleRepository.findAllByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleUserResponseDTO> findByRoleId(Long roleId) {
        findRole(roleId);
        return userRoleMapper.toRoleUserResponseDTOList(userRoleRepository.findAllByRoleId(roleId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAvailableUsers(Long roleId) {
        findRole(roleId);
        return userMapper.toResponseDTOList(userRepository.findAllWithoutRole(roleId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDTO> findAvailableRoles(Long userId) {
        findUser(userId);
        return roleMapper.toResponseDTOList(roleRepository.findAllNotAssignedToUser(userId));
    }

    // Retirar la asignación no elimina al usuario ni al rol.
    @Override
    @Transactional
    public void delete(Long userId, Long roleId, Long currentUserId) {
        if (userId.equals(currentUserId)) {
            throw new ApplicationException(
                    ErrorCase.INVALID_OPERATION,
                    "No puede retirar roles de su propio usuario"
            );
        }
        userRoleRepository.delete(findAssignment(userId, roleId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Usuario no encontrado"
                ));
    }

    private Role findRole(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Rol no encontrado"
                ));
    }

    private UserRole findAssignment(Long userId, Long roleId) {
        return userRoleRepository.findByUserIdAndRoleId(userId, roleId)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Asignación de rol no encontrada"
                ));
    }
}
