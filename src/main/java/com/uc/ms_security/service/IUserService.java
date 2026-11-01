package com.uc.ms_security.service;

import com.uc.ms_security.dto.user.CreateUserDTO;
import com.uc.ms_security.dto.user.UpdateUserDTO;
import com.uc.ms_security.dto.user.UserDetailResponseDTO;
import com.uc.ms_security.dto.user.UserResponseDTO;
import com.uc.ms_security.dto.user.UserRolesResponseDTO;
import com.uc.ms_security.dto.user.UserSessionsResponseDTO;

import java.util.List;

public interface IUserService {

    UserResponseDTO create(CreateUserDTO dto);

    List<UserResponseDTO> findAll();

    UserResponseDTO findById(Long id);

    UserDetailResponseDTO findByIdWithProfile(Long id);

    UserSessionsResponseDTO findByIdAndSessions(Long id);

    UserRolesResponseDTO findByIdAndRoles(Long id);

    UserResponseDTO update(Long id, UpdateUserDTO dto);

    UserResponseDTO deactivate(Long id, Long currentUserId);

    UserResponseDTO activate(Long id);
}
