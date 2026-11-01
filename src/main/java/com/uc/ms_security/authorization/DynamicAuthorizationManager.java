package com.uc.ms_security.authorization;

import com.uc.ms_security.service.IAccessControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class DynamicAuthorizationManager
        implements AuthorizationManager<RequestAuthorizationContext> {

    private final IAccessControlService accessControlService;

    @Override
    public AuthorizationResult authorize(
            Supplier<? extends Authentication> authenticationSupplier,
            RequestAuthorizationContext context) {
        boolean permitted = AuthenticatedUserId.from(authenticationSupplier.get())
                .map(userId -> accessControlService.hasPermission(
                        userId,
                        context.getRequest().getMethod(),
                        context.getRequest().getRequestURI()
                ))
                .orElse(false);
        return new AuthorizationDecision(permitted);
    }
}
