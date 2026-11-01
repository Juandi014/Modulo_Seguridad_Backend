package com.uc.ms_security.service;

import com.uc.ms_security.dto.role.RoleResponseDTO;
import com.uc.ms_security.dto.user.UserResponseDTO;
import com.uc.ms_security.dto.userrole.AssignRoleRequestDTO;
import com.uc.ms_security.dto.userrole.RoleUserResponseDTO;
import com.uc.ms_security.dto.userrole.UserRoleResponseDTO;

import java.util.List;

public interface IUserRoleService {

    UserRoleResponseDTO assign(AssignRoleRequestDTO dto);

    List<UserRoleResponseDTO> findAll();

    List<UserRoleResponseDTO> findByUserId(Long userId);

    List<RoleUserResponseDTO> findByRoleId(Long roleId);

    List<UserResponseDTO> findAvailableUsers(Long roleId);

    List<RoleResponseDTO> findAvailableRoles(Long userId);

    void delete(Long userId, Long roleId, Long currentUserId);
}
