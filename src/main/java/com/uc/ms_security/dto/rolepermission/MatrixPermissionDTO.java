package com.uc.ms_security.dto.rolepermission;

import com.uc.ms_security.entity.enums.PermissionModule;
import com.uc.ms_security.entity.enums.PermissionOperation;
import lombok.Value;

// Un permiso del sistema dentro de la matriz, indicando si el rol lo tiene.
@Value
public class MatrixPermissionDTO {

    Long id;

    PermissionModule module;

    String resource;

    PermissionOperation operation;

    String method;

    String url;

    boolean assigned;
}
