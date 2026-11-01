package com.uc.ms_security.dto.user;

import com.uc.ms_security.validation.ValidPassword;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDTO extends BaseUserDTO {

    @ValidPassword
    private String password;
}
