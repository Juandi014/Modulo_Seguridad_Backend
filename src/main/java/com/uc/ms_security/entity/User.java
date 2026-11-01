package com.uc.ms_security.entity;

import com.uc.ms_security.entity.enums.AuthProvider;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_provider",
                columnNames = {"auth_provider", "provider_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
            nullable = false,
            length = 100
    )
    private String name;

    @Column(
            nullable = false,
            unique = true,
            length = 150
    )
    private String email;

    // Nula para usuarios OAuth: no tienen contraseña propia en la plataforma.
    @Column(
            length = 255
    )
    private String password;

    // Identificador del usuario en el proveedor OAuth (nulo si es LOCAL).
    // Con auth_provider + provider_id se reconoce al usuario en cada acceso.
    @Column(
            name = "provider_id",
            length = 100
    )
    private String providerId;

    // Los usuarios no se eliminan: se desactivan. Un usuario inactivo
    // no puede iniciar sesión ni ejecutar operaciones protegidas.
    @Column(
            nullable = false
    )
    @ColumnDefault("true")
    private boolean active = true;

    // Cómo se autentica el usuario: LOCAL (correo + contraseña) u OAuth.
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(
            name = "auth_provider",
            nullable = false,
            length = 20
    )
    @ColumnDefault("'LOCAL'")
    private AuthProvider authProvider = AuthProvider.LOCAL;

    // Autenticación de dos factores (solo cuentas LOCAL)
    @Column(
            name = "two_factor_enabled",
            nullable = false
    )
    @ColumnDefault("false")
    private boolean twoFactorEnabled = false;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Profile profile;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Session> sessions = new ArrayList<>();

        @OneToMany(
                        mappedBy = "user", //En el otro lado aparecerá como user, es decir, en la entidad UserRole habrá un atributo llamado user que será el propietario de la relación.
                        cascade = CascadeType.ALL,
                        orphanRemoval = true,
                        fetch = FetchType.LAZY
        )
        private List<UserRole> userRoles = new ArrayList<>();
}