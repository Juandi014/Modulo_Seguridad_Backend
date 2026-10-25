package com.uc.ms_security.service.impl;

import com.uc.ms_security.authorization.UrlPatternNormalizer;
import com.uc.ms_security.dto.permission.PermissionRequestDTO;
import com.uc.ms_security.dto.permission.PermissionResponseDTO;
import com.uc.ms_security.entity.Permission;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.mapper.PermissionMapper;
import com.uc.ms_security.repository.PermissionRepository;
import com.uc.ms_security.repository.RolePermissionRepository;
import com.uc.ms_security.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements IPermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionMapper permissionMapper;
    private final UrlPatternNormalizer urlPatternNormalizer;

    @Override
    public PermissionResponseDTO create(PermissionRequestDTO dto) {
        String url = urlPatternNormalizer.normalizeUrl(dto.getUrl());
        String method = urlPatternNormalizer.normalizeMethod(dto.getMethod());
        if (permissionRepository.existsByUrlAndMethod(url, method)) {
            throw duplicated(method, url);
        }
        Permission permission = permissionMapper.toEntity(dto);
        permission.setUrl(url);
        permission.setMethod(method);
        return permissionMapper.toResponseDTO(permissionRepository.save(permission));
    }

    @Override
    public List<PermissionResponseDTO> findAll() {
        return permissionMapper.toResponseDTOList(permissionRepository.findAll(
                Sort.by("module", "resource", "operation", "url")
        ));
    }

    @Override
    public PermissionResponseDTO findById(Long id) {
        return permissionMapper.toResponseDTO(findEntityById(id));
    }

    @Override
    public PermissionResponseDTO update(Long id, PermissionRequestDTO dto) {
        Permission permission = findEntityById(id);
        String url = urlPatternNormalizer.normalizeUrl(dto.getUrl());
        String method = urlPatternNormalizer.normalizeMethod(dto.getMethod());
        if (permissionRepository.existsByUrlAndMethodAndIdNot(url, method, id)) {
            throw duplicated(method, url);
        }
        permissionMapper.updateEntity(dto, permission);
        permission.setUrl(url);
        permission.setMethod(method);
        return permissionMapper.toResponseDTO(permissionRepository.save(permission));
    }

    // Un permiso asignado a roles no se elimina: primero hay que retirarlo
    // de esos roles. Así eliminar un permiso nunca afecta a los roles.
    @Override
    public void delete(Long id) {
        Permission permission = findEntityById(id);
        long assignedRoles = rolePermissionRepository.countByPermissionId(id);
        if (assignedRoles > 0) {
            throw new ApplicationException(
                    ErrorCase.CONFLICT,
                    "No se puede eliminar el permiso porque está asignado a "
                            + assignedRoles + " rol(es). Retírelo primero de esos roles"
            );
        }
        permissionRepository.delete(permission);
    }

    private Permission findEntityById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Permiso no encontrado"
                ));
    }

    private ApplicationException duplicated(String method, String url) {
        return new ApplicationException(
                ErrorCase.ALREADY_EXISTS,
                "Ya existe un permiso para " + method + " " + url
        );
    }
}
