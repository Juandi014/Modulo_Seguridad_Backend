package com.uc.ms_security.entity;

import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.enums.LoginAttemptResult;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

// Registro de auditoría: cada intento de inicio de sesión, exitoso o fallido.
// Solo se inserta; nunca se modifica ni se elimina desde la aplicación.
@Entity
@Table(
        name = "login_attempts",
        indexes = @Index(name = "idx_login_attempt_created", columnList = "created_at")
)
@Getter
@Setter
@NoArgsConstructor
public class LoginAttempt {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    // Correo escrito o entregado por el proveedor (puede no existir en el sistema)
    @Column(
            length = 150
    )
    private String email;

    // Usuario identificado, si existe
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "user_id"
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(
            nullable = false,
            length = 20
    )
    private AuthProvider method;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(
            nullable = false,
            length = 30
    )
    private LoginAttemptResult result;

    @Column(
            name = "ip_address",
            length = 45
    )
    private String ipAddress;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;
}
