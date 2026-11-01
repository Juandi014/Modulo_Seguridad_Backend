package com.uc.ms_security.authentication.oauth;

import com.uc.ms_security.dto.auth.OAuthProfileDTO;
import com.uc.ms_security.entity.enums.AuthProvider;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

// Google (OpenID Connect): sub, email, email_verified, name
@Component
public class GoogleProfileExtractor implements IOAuthProfileExtractor {

    @Override
    public AuthProvider provider() {
        return AuthProvider.GOOGLE;
    }

    @Override
    public OAuthProfileDTO extract(OAuth2User oauthUser, String accessToken) {
        Boolean verified = oauthUser.getAttribute("email_verified");
        String email = Boolean.TRUE.equals(verified) ? oauthUser.getAttribute("email") : null;
        return new OAuthProfileDTO(
                AuthProvider.GOOGLE,
                oauthUser.getAttribute("sub"),
                email,
                oauthUser.getAttribute("name")
        );
    }
}
