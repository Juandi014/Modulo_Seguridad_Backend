package com.uc.ms_security.authentication.oauth;

import java.util.List;

// Proveedores OAuth habilitados (los que tienen credenciales configuradas).
public interface IOAuthProviderRegistry {

    List<String> enabledProviders();
}
