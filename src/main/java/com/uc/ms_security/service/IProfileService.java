package com.uc.ms_security.service;

import com.uc.ms_security.dto.profile.ProfileRequestDTO;
import com.uc.ms_security.dto.profile.ProfileResponseDTO;

public interface IProfileService {

    ProfileResponseDTO create(Long userId, ProfileRequestDTO dto);

    ProfileResponseDTO update(Long userId, ProfileRequestDTO dto);
}
