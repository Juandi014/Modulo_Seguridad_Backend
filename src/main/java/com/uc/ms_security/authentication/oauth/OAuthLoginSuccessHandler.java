package com.uc.ms_security.authentication.oauth;

import com.uc.ms_security.dto.auth.AuthResponseDTO;
import com.uc.ms_security.dto.auth.OAuthProfileDTO;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.service.IOAuthExchangeCodeStore;
import com.uc.ms_security.service.IOAuthLoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Se ejecuta cuando el proveedor ya validó la identidad. Convierte su
 * respuesta en un OAuthProfileDTO, abre la sesión interna y redirige al
 * frontend con un código de un solo uso (o con el mensaje de error).
 */
@Component
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final IOAuthLoginService oauthLoginService;
    private final IOAuthExchangeCodeStore exchangeCodeStore;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final Map<String, IOAuthProfileExtractor> extractors;
    private final String frontendUrl;

    public OAuthLoginSuccessHandler(
            IOAuthLoginService oauthLoginService,
            IOAuthExchangeCodeStore exchangeCodeStore,
            OAuth2AuthorizedClientService authorizedClientService,
            List<IOAuthProfileExtractor> extractors,
            @Value("${security.frontend-url}") String frontendUrl) {
        this.oauthLoginService = oauthLoginService;
        this.exchangeCodeStore = exchangeCodeStore;
        this.authorizedClientService = authorizedClientService;
        this.extractors = extractors.stream().collect(Collectors.toMap(
                extractor -> extractor.provider().name().toLowerCase(),
                Function.identity()
        ));
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        String target;
        try {
            IOAuthProfileExtractor extractor = extractors.get(registrationId);
            OAuthProfileDTO profile = extractor.extract(
                    oauthToken.getPrincipal(),
                    accessToken(registrationId, oauthToken.getName())
            );
            AuthResponseDTO session = oauthLoginService.login(profile);
            target = frontendUrl + "/#/oauth-callback?code=" + exchangeCodeStore.save(session);
        } catch (ApplicationException exception) {
            target = frontendUrl + "/#/login?oauthError="
                    + URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
        } catch (RuntimeException exception) {
            // Fallo inesperado: se registra en consola y se vuelve al login con un aviso
            exception.printStackTrace();
            target = frontendUrl + "/#/login?oauthError=" + URLEncoder.encode(
                    "Ocurrió un error inesperado al iniciar sesión con el proveedor externo",
                    StandardCharsets.UTF_8);
        } finally {
            // No se conserva nada del proveedor en el servidor
            authorizedClientService.removeAuthorizedClient(registrationId, oauthToken.getName());
            HttpSession httpSession = request.getSession(false);
            if (httpSession != null) {
                httpSession.invalidate();
            }
        }
        response.sendRedirect(target);
    }

    private String accessToken(String registrationId, String principalName) {
        OAuth2AuthorizedClient client =
                authorizedClientService.loadAuthorizedClient(registrationId, principalName);
        return client != null ? client.getAccessToken().getTokenValue() : null;
    }
}
