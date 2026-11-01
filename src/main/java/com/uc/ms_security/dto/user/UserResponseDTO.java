package com.uc.ms_security.dto.user;

import com.uc.ms_security.entity.enums.AuthProvider;
import lombok.Value;

@Value
public class UserResponseDTO {
    Long id;
    String name;
    String email;
    boolean active;
    AuthProvider authProvider;
}
