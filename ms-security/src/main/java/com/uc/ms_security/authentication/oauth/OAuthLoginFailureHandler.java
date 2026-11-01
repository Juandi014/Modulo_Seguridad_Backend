package com.uc.ms_security.authentication.oauth;

import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.enums.LoginAttemptResult;
import com.uc.ms_security.service.ILoginAuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

// El proveedor rechazó o el usuario canceló: se audita y se vuelve al login con un aviso.
@Component
public class OAuthLoginFailureHandler implements AuthenticationFailureHandler {

    private final ILoginAuditService loginAuditService;
    private final String frontendUrl;

    public OAuthLoginFailureHandler(
            ILoginAuditService loginAuditService,
            @Value("${security.frontend-url}") String frontendUrl) {
        this.loginAuditService = loginAuditService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        AuthProvider provider = providerFromCallback(request.getRequestURI());
        if (provider != null) {
            // El proveedor no entregó la identidad, por eso no hay correo ni usuario
            loginAuditService.record(null, null, provider, LoginAttemptResult.OAUTH_REJECTED);
        }
        String message = "No fue posible iniciar sesión con el proveedor externo. "
                + "Es posible que haya cancelado el acceso";
        response.sendRedirect(frontendUrl + "/#/login?oauthError="
                + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }

    // /login/oauth2/code/google -> GOOGLE
    private AuthProvider providerFromCallback(String uri) {
        String registrationId = uri.substring(uri.lastIndexOf('/') + 1);
        try {
            return AuthProvider.valueOf(registrationId.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
