package com.uc.ms_security.dto.permission;

import com.uc.ms_security.entity.enums.PermissionModule;
import com.uc.ms_security.entity.enums.PermissionOperation;
import lombok.Value;

@Value
public class PermissionResponseDTO {

    Long id;

    String url;

    String method;

    PermissionModule module;

    String resource;

    PermissionOperation operation;
}
