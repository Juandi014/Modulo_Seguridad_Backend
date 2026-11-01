package com.uc.ms_security.dto.profile;

import lombok.Value;

@Value
public class ProfileResponseDTO {
    Long id;
    String phone;
    boolean hasPhoto;
}
