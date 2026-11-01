package com.uc.ms_security.authentication.oauth;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Registro de clientes OAuth que admite estar vacío: solo contiene los
 * proveedores con credenciales en variables de entorno. (El repositorio
 * por defecto de Spring exige al menos uno y no dejaría arrancar la app.)
 */
public class OAuthProviderRegistryImpl
        implements ClientRegistrationRepository, IOAuthProviderRegistry {

    private final Map<String, ClientRegistration> registrations;

    public OAuthProviderRegistryImpl(List<ClientRegistration> registrations) {
        Map<String, ClientRegistration> map = new LinkedHashMap<>();
        registrations.forEach(registration -> map.put(registration.getRegistrationId(), registration));
        this.registrations = Collections.unmodifiableMap(map);
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        return registrations.get(registrationId);
    }

    @Override
    public List<String> enabledProviders() {
        return List.copyOf(registrations.keySet());
    }
}
