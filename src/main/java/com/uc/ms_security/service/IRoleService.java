package com.uc.ms_security.service;

import com.uc.ms_security.dto.role.RoleRequestDTO;
import com.uc.ms_security.dto.role.RoleResponseDTO;
import com.uc.ms_security.dto.rolepermission.RolePermissionsResponseDTO;

import java.util.List;

public interface IRoleService {

    RoleResponseDTO create(RoleRequestDTO dto);

    List<RoleResponseDTO> findAll();

    RoleResponseDTO findById(Long id);

    RolePermissionsResponseDTO findByIdAndPermissions(Long id);

    RoleResponseDTO update(Long id, RoleRequestDTO dto);

    void delete(Long id);

    RoleResponseDTO deactivate(Long id, Long currentUserId);

    RoleResponseDTO activate(Long id);
}
