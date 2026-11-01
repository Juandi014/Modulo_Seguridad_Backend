package com.uc.ms_security.authentication.oauth;

import com.uc.ms_security.dto.auth.OAuthProfileDTO;
import com.uc.ms_security.entity.enums.AuthProvider;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

// GitHub: id, name (o login). El correo puede ser privado, por eso se
// consulta /user/emails y se toma el principal verificado.
@Component
public class GitHubProfileExtractor implements IOAuthProfileExtractor {

    private static final String EMAILS_URL = "https://api.github.com/user/emails";

    private final RestClient restClient = RestClient.create();

    @Override
    public AuthProvider provider() {
        return AuthProvider.GITHUB;
    }

    @Override
    public OAuthProfileDTO extract(OAuth2User oauthUser, String accessToken) {
        Object id = oauthUser.getAttribute("id");
        String name = oauthUser.getAttribute("name");
        if (name == null || name.isBlank()) {
            name = oauthUser.getAttribute("login");
        }
        return new OAuthProfileDTO(
                AuthProvider.GITHUB,
                id != null ? id.toString() : null,
                findPrimaryVerifiedEmail(accessToken),
                name
        );
    }

    private String findPrimaryVerifiedEmail(String accessToken) {
        if (accessToken == null) {
            return null;
        }
        try {
            List<Map<String, Object>> emails = restClient.get()
                    .uri(EMAILS_URL)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/vnd.github+json")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() { });
            if (emails == null) {
                return null;
            }
            return emails.stream()
                    .filter(email -> Boolean.TRUE.equals(email.get("primary"))
                            && Boolean.TRUE.equals(email.get("verified")))
                    .map(email -> (String) email.get("email"))
                    .findFirst()
                    .orElse(null);
        } catch (RestClientException exception) {
            return null;
        }
    }
}
