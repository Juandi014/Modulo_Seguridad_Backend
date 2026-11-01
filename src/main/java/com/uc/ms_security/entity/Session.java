package com.uc.ms_security.entity;

import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.enums.SessionStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

// Sesión interna: se crea en cada inicio de sesión exitoso, sin importar
// el mecanismo (credenciales u OAuth). El token del JWT solo es válido
// mientras su sesión exista, no haya expirado y no se haya cerrado.
@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
public class Session {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    // Identificador único del token (claim "jti" del JWT)
    @Column(
            nullable = false,
            unique = true,
            length = 500
    )
    private String token;

    @Column(
            nullable = false
    )
    private LocalDateTime expiration;

    @Column(
            name = "created_at"
    )
    private LocalDateTime createdAt;

    // Fecha de cierre (logout); nula mientras la sesión siga abierta
    @Column(
            name = "revoked_at"
    )
    private LocalDateTime revokedAt;

    // Mecanismo con el que se abrió la sesión
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(
            name = "auth_method",
            length = 20
    )
    private AuthProvider authMethod;

    // Solo ACTIVE da acceso. Mientras falte el 2FA la sesión está PENDING_2FA.
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(
            nullable = false,
            length = 20
    )
    @ColumnDefault("'ACTIVE'")
    private SessionStatus status = SessionStatus.ACTIVE;

    // Código 2FA asociado a ESTE inicio de sesión: se guarda su hash,
    // vence con la sesión pendiente (5 min) y se borra al usarse.
    @Column(
            name = "code_2fa_hash",
            length = 64
    )
    private String twoFactorCodeHash;

    @Column(
            name = "code_2fa_attempts"
    )
    private Integer twoFactorAttempts;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;
}
