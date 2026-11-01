package com.uc.ms_security.mapper;

import com.uc.ms_security.dto.profile.ProfileRequestDTO;
import com.uc.ms_security.dto.profile.ProfileResponseDTO;
import com.uc.ms_security.entity.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public Profile toEntity(ProfileRequestDTO dto) {
        Profile profile = new Profile();
        profile.setPhone(normalizePhone(dto.getPhone()));
        return profile;
    }

    public void updateEntity(ProfileRequestDTO dto, Profile profile) {
        profile.setPhone(normalizePhone(dto.getPhone()));
    }

    public ProfileResponseDTO toResponseDTO(Profile profile) {
        if (profile == null) {
            return null;
        }

        return new ProfileResponseDTO(
                profile.getId(),
                profile.getPhone(),
                profile.getPhotoFilename() != null
        );
    }

    // Un teléfono vacío se guarda como null
    private String normalizePhone(String phone) {
        return (phone == null || phone.isBlank()) ? null : phone.trim();
    }
}
