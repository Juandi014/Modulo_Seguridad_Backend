package com.uc.ms_security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.uc.ms_security.authentication.oauth.OAuthLoginFailureHandler;
import com.uc.ms_security.authentication.oauth.OAuthLoginSuccessHandler;
import com.uc.ms_security.authorization.ActiveUserAuthorizationManager;
import com.uc.ms_security.authorization.DynamicAuthorizationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final DynamicAuthorizationManager dynamicAuthorizationManager;
    private final ActiveUserAuthorizationManager activeUserAuthorizationManager;
    private final OAuthLoginSuccessHandler oauthLoginSuccessHandler;
    private final OAuthLoginFailureHandler oauthLoginFailureHandler;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        // Públicos: login, registro y flujo OAuth
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/oauth/providers",
                                "/api/auth/oauth/exchange",
                                "/api/auth/password-recovery/**",
                                "/api/auth/2fa/verify",
                                "/api/auth/2fa/cancel",
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()
                        // Cerrar la propia sesión: basta con estar autenticado
                        .requestMatchers("/api/auth/logout").authenticated()
                        // Rutas personales: cualquier usuario activo, sobre su propia cuenta
                        .requestMatchers("/api/profile/me", "/api/profile/me/**", "/api/account/**")
                                .access(activeUserAuthorizationManager)
                        .anyRequest().access(dynamicAuthorizationManager)
                )
                // Login con Google, GitHub o Facebook (ver OAuthClientConfig)
                .oauth2Login(oauth -> oauth
                        .authorizedClientService(authorizedClientService)
                        .successHandler(oauthLoginSuccessHandler)
                        .failureHandler(oauthLoginFailureHandler)
                )
                .oauth2ResourceServer(resourceServer ->
                        resourceServer.jwt(
                                Customizer.withDefaults()
                        )
                )
                // La API responde 401 (no redirige a una página de login)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                );

        return http.build();
    }
}
