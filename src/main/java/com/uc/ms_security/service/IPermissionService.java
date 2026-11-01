package com.uc.ms_security.service;

import com.uc.ms_security.dto.permission.PermissionRequestDTO;
import com.uc.ms_security.dto.permission.PermissionResponseDTO;

import java.util.List;

public interface IPermissionService {

    PermissionResponseDTO create(PermissionRequestDTO dto);

    List<PermissionResponseDTO> findAll();

    PermissionResponseDTO findById(Long id);

    PermissionResponseDTO update(Long id, PermissionRequestDTO dto);

    void delete(Long id);
}
