package com.uc.ms_security.service;

public interface IAccessControlService {

    /**
     * Indica si al menos uno de los roles activos del usuario (activo)
     * tiene el permiso para el método y la URL solicitados.
     */
    boolean hasPermission(Long userId, String httpMethod, String requestUrl);

    /** Indica si el usuario existe y sigue activo. */
    boolean isActiveUser(Long userId);
}
