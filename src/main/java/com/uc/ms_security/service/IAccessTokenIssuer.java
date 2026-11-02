package com.uc.ms_security.service;

import com.uc.ms_security.dto.auth.IssuedTokenDTO;
import com.uc.ms_security.entity.User;

/**
 * Emite el token interno de acceso. Es el mismo sin importar cómo se
 * autenticó el usuario (credenciales u OAuth), como propone la guía 7:
 * JwtService -> AccessTokenIssuer (interfaz) -> JwtAccessTokenIssuer.
 */
public interface IAccessTokenIssuer {

    IssuedTokenDTO issue(User user);

    /**
     * Emite un token nuevo que conserva el identificador (jti) de la sesión,
     * con una expiración contada desde ahora. Se usa al renovar la sesión.
     */
    IssuedTokenDTO renew(User user, String tokenId);
}
