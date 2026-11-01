package com.uc.ms_security.authentication.oauth;

import com.uc.ms_security.dto.auth.OAuthProfileDTO;
import com.uc.ms_security.entity.enums.AuthProvider;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Cada proveedor entrega sus datos con nombres distintos (sub/id, name/login...).
 * Una implementación por proveedor los convierte en un OAuthProfileDTO común.
 * Agregar un proveedor nuevo = agregar una implementación.
 */
public interface IOAuthProfileExtractor {

    AuthProvider provider();

    OAuthProfileDTO extract(OAuth2User oauthUser, String accessToken);
}
