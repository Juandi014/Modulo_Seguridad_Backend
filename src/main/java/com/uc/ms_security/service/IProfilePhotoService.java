package com.uc.ms_security.service;

import com.uc.ms_security.dto.profile.ProfilePhotoDTO;
import com.uc.ms_security.dto.profile.ProfileResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IProfilePhotoService {

    ProfileResponseDTO upload(Long userId, MultipartFile file);

    ProfilePhotoDTO download(Long userId);

    ProfileResponseDTO remove(Long userId);
}
