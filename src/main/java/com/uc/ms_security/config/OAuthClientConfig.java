package com.uc.ms_security.config;

import com.uc.ms_security.authentication.oauth.OAuthProviderRegistryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.util.ArrayList;
import java.util.List;

// Proveedores OAuth: Google, GitHub y Facebook. Las credenciales llegan por
// variables de entorno; un proveedor sin credenciales queda deshabilitado.
@Configuration
public class OAuthClientConfig {

    @Bean
    public OAuthProviderRegistryImpl clientRegistrationRepository(
            @Value("${security.oauth.google.client-id}") String googleId,
            @Value("${security.oauth.google.client-secret}") String googleSecret,
            @Value("${security.oauth.github.client-id}") String githubId,
            @Value("${security.oauth.github.client-secret}") String githubSecret,
            @Value("${security.oauth.facebook.client-id}") String facebookId,
            @Value("${security.oauth.facebook.client-secret}") String facebookSecret) {

        List<ClientRegistration> registrations = new ArrayList<>();

        if (configured(googleId, googleSecret)) {
            registrations.add(CommonOAuth2Provider.GOOGLE.getBuilder("google")
                    .clientId(googleId)
                    .clientSecret(googleSecret)
                    .build());
        }
        if (configured(githubId, githubSecret)) {
            registrations.add(CommonOAuth2Provider.GITHUB.getBuilder("github")
                    .clientId(githubId)
                    .clientSecret(githubSecret)
                    .scope("read:user", "user:email")
                    .build());
        }
        if (configured(facebookId, facebookSecret)) {
            registrations.add(CommonOAuth2Provider.FACEBOOK.getBuilder("facebook")
                    .clientId(facebookId)
                    .clientSecret(facebookSecret)
                    .build());
        }

        return new OAuthProviderRegistryImpl(registrations);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
            OAuthProviderRegistryImpl clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    private boolean configured(String clientId, String clientSecret) {
        return clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank();
    }
}
