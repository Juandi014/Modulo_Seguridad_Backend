package com.uc.ms_security.entity;

import com.uc.ms_security.entity.enums.PermissionModule;
import com.uc.ms_security.entity.enums.PermissionOperation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

// Un permiso autoriza un endpoint (método + url) y se clasifica por
// módulo, recurso y operación para mostrarlo en la matriz de roles.
// Solo método + url es único: una misma casilla (recurso + operación)
// puede agrupar varios endpoints.
@Entity
@Table(
        name = "permissions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_permission_url_method",
                columnNames = {"url", "method"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String url;

    @Column(nullable = false, length = 10)
    private String method;

    // Columnas nulas en BD solo para no romper permisos creados antes de
    // esta versión; el DTO las exige al crear o actualizar.
    // VARCHAR en vez de ENUM nativo de MySQL, para poder agregar valores
    // al enum sin migrar la columna.
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 30)
    private PermissionModule module;

    @Column(length = 50)
    private String resource;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 20)
    private PermissionOperation operation;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
    private List<RolePermission> rolePermissions = new ArrayList<>();
}
