package com.uc.ms_security.service.impl;

import com.uc.ms_security.dto.role.RoleRequestDTO;
import com.uc.ms_security.dto.role.RoleResponseDTO;
import com.uc.ms_security.dto.rolepermission.RolePermissionsResponseDTO;
import com.uc.ms_security.entity.Role;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.mapper.RoleMapper;
import com.uc.ms_security.repository.RolePermissionRepository;
import com.uc.ms_security.repository.RoleRepository;
import com.uc.ms_security.repository.UserRoleRepository;
import com.uc.ms_security.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponseDTO create(RoleRequestDTO dto) {
        if (roleRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new ApplicationException(
                    ErrorCase.ALREADY_EXISTS,
                    "Ya existe un rol con ese nombre"
            );
        }

        Role role = roleMapper.toEntity(dto);
        return roleMapper.toResponseDTO(roleRepository.save(role));
    }

    @Override
    public List<RoleResponseDTO> findAll() {
        return roleMapper.toResponseDTOList(roleRepository.findAll());
    }

    @Override
    public RoleResponseDTO findById(Long id) {
        return roleMapper.toResponseDTO(findEntityById(id));
    }

    @Override
    public RolePermissionsResponseDTO findByIdAndPermissions(Long id) {
        Role role = roleRepository.findWithPermissionsById(id)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Rol no encontrado con id: " + id
                ));
        return roleMapper.toPermissionsResponseDTO(role);
    }

    @Override
    public RoleResponseDTO update(Long id, RoleRequestDTO dto) {
        Role role = findEntityById(id);

        if (roleRepository.existsByNameIgnoreCaseAndIdNot(dto.getName().trim(), id)) {
            throw new ApplicationException(
                    ErrorCase.ALREADY_EXISTS,
                    "Ya existe un rol con ese nombre"
            );
        }

        roleMapper.updateEntity(dto, role);
        return roleMapper.toResponseDTO(roleRepository.save(role));
    }

    // Un rol asignado a usuarios no se puede eliminar. Si no tiene usuarios,
    // se retiran sus asociaciones con permisos (los permisos siguen existiendo
    // en el sistema) y luego se elimina el rol.
    @Override
    @Transactional
    public void delete(Long id) {
        Role role = findEntityById(id);

        long assignedUsers = userRoleRepository.countByRoleId(id);
        if (assignedUsers > 0) {
            throw new ApplicationException(
                    ErrorCase.CONFLICT,
                    "No se puede eliminar el rol porque está asignado a "
                            + assignedUsers + " usuario(s)"
            );
        }

        rolePermissionRepository.deleteAll(
                rolePermissionRepository.findAllByRoleId(id)
        );
        roleRepository.delete(role);
    }

    @Override
    public RoleResponseDTO deactivate(Long id, Long currentUserId) {
        Role role = findEntityById(id);
        if (userRoleRepository.existsByUserIdAndRoleId(currentUserId, id)) {
            throw new ApplicationException(
                    ErrorCase.INVALID_OPERATION,
                    "No puede desactivar un rol que tiene asignado su propio usuario"
            );
        }
        if (!role.isActive()) {
            throw new ApplicationException(
                    ErrorCase.CONFLICT,
                    "El rol ya se encuentra desactivado"
            );
        }
        role.setActive(false);
        return roleMapper.toResponseDTO(roleRepository.save(role));
    }

    @Override
    public RoleResponseDTO activate(Long id) {
        Role role = findEntityById(id);
        if (role.isActive()) {
            throw new ApplicationException(
                    ErrorCase.CONFLICT,
                    "El rol ya se encuentra activo"
            );
        }
        role.setActive(true);
        return roleMapper.toResponseDTO(roleRepository.save(role));
    }

    private Role findEntityById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Rol no encontrado"
                ));
    }
}
