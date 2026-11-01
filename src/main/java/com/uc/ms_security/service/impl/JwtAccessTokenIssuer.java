package com.uc.ms_security.service.impl;

import com.uc.ms_security.dto.auth.IssuedTokenDTO;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.service.IAccessTokenIssuer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

// Antes JwtService (guía 7). Firma el JWT con JwtEncoder.
@Service
public class JwtAccessTokenIssuer implements IAccessTokenIssuer {

    private final JwtEncoder jwtEncoder;
    private final Duration tokenDuration;

    public JwtAccessTokenIssuer(
            JwtEncoder jwtEncoder,
            @Value("${security.session.duration-minutes}") long durationMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.tokenDuration = Duration.ofMinutes(durationMinutes);
    }

    @Override
    public IssuedTokenDTO issue(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(tokenDuration);
        String tokenId = UUID.randomUUID().toString();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("ms-security")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .id(tokenId)
                .claim("email", user.getEmail())
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        Jwt jwt = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        );

        return new IssuedTokenDTO(jwt.getTokenValue(), tokenId, expiresAt);
    }
}
