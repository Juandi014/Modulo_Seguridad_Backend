package com.uc.ms_security.authentication.oauth;

import com.uc.ms_security.dto.auth.OAuthProfileDTO;
import com.uc.ms_security.entity.enums.AuthProvider;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

// Facebook: id, name, email (puede no venir si la cuenta no lo comparte)
@Component
public class FacebookProfileExtractor implements IOAuthProfileExtractor {

    @Override
    public AuthProvider provider() {
        return AuthProvider.FACEBOOK;
    }

    @Override
    public OAuthProfileDTO extract(OAuth2User oauthUser, String accessToken) {
        return new OAuthProfileDTO(
                AuthProvider.FACEBOOK,
                oauthUser.getAttribute("id"),
                oauthUser.getAttribute("email"),
                oauthUser.getAttribute("name")
        );
    }
}
