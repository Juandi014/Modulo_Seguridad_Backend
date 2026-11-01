package com.uc.ms_security.config;

import com.uc.ms_security.entity.Permission;
import com.uc.ms_security.entity.enums.PermissionModule;
import com.uc.ms_security.entity.enums.PermissionOperation;
import com.uc.ms_security.entity.Role;
import com.uc.ms_security.entity.RolePermission;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.entity.UserRole;
import com.uc.ms_security.repository.PermissionRepository;
import com.uc.ms_security.repository.RolePermissionRepository;
import com.uc.ms_security.repository.RoleRepository;
import com.uc.ms_security.repository.UserRepository;
import com.uc.ms_security.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static com.uc.ms_security.entity.enums.PermissionOperation.CREATE;
import static com.uc.ms_security.entity.enums.PermissionOperation.DELETE;
import static com.uc.ms_security.entity.enums.PermissionOperation.LIST;
import static com.uc.ms_security.entity.enums.PermissionOperation.UPDATE;
import static com.uc.ms_security.entity.enums.PermissionOperation.VIEW;

@Configuration
@RequiredArgsConstructor
public class DatabaseSeeder {

    private static final String USERS = "Usuarios";
    private static final String PROFILES = "Perfiles";
    private static final String SESSIONS = "Sesiones";
    private static final String AUDIT = "Auditoría de accesos";
    private static final String ROLES = "Roles";
    private static final String USER_ROLES = "Asignación de roles";
    private static final String PERMISSIONS = "Permisos";
    private static final String ROLE_PERMISSIONS = "Permisos de roles";

    private static final PermissionSeed[] ADMIN_PERMISSIONS = {
            new PermissionSeed("GET", "/api/users", USERS, LIST),
            new PermissionSeed("POST", "/api/users", USERS, CREATE),
            new PermissionSeed("GET", "/api/users/{id}", USERS, VIEW),
            new PermissionSeed("PUT", "/api/users/{id}", USERS, UPDATE),
            new PermissionSeed("PATCH", "/api/users/{id}/activate", USERS, UPDATE),
            // Desactivar es la eliminación lógica del usuario (HU-SE-01)
            new PermissionSeed("PATCH", "/api/users/{id}/deactivate", USERS, DELETE),
            new PermissionSeed("GET", "/api/users/{id}/profile", PROFILES, VIEW),
            new PermissionSeed("GET", "/api/users/{id}/profile/photo", PROFILES, VIEW),
            new PermissionSeed("POST", "/api/users/{id}/profile", PROFILES, CREATE),
            new PermissionSeed("PUT", "/api/users/{id}/profile", PROFILES, UPDATE),
            new PermissionSeed("PUT", "/api/users/{id}/profile/photo", PROFILES, UPDATE),
            new PermissionSeed("DELETE", "/api/users/{id}/profile/photo", PROFILES, DELETE),
            new PermissionSeed("GET", "/api/users/{id}/sessions", SESSIONS, LIST),
            new PermissionSeed("PATCH", "/api/users/{id}/sessions/{id}/revoke", SESSIONS, DELETE),
            new PermissionSeed("GET", "/api/roles", ROLES, LIST),
            new PermissionSeed("POST", "/api/roles", ROLES, CREATE),
            new PermissionSeed("GET", "/api/roles/{id}", ROLES, VIEW),
            new PermissionSeed("PUT", "/api/roles/{id}", ROLES, UPDATE),
            new PermissionSeed("PATCH", "/api/roles/{id}/deactivate", ROLES, UPDATE),
            new PermissionSeed("PATCH", "/api/roles/{id}/activate", ROLES, UPDATE),
            new PermissionSeed("DELETE", "/api/roles/{id}", ROLES, DELETE),
            new PermissionSeed("GET", "/api/user-roles/users/{id}", USER_ROLES, LIST),
            new PermissionSeed("GET", "/api/user-roles/roles/{id}", USER_ROLES, LIST),
            new PermissionSeed("POST", "/api/user-roles", USER_ROLES, CREATE),
            new PermissionSeed("GET", "/api/user-roles/users/{id}/available-roles", USER_ROLES, CREATE),
            new PermissionSeed("GET", "/api/user-roles/roles/{id}/available-users", USER_ROLES, CREATE),
            new PermissionSeed("DELETE", "/api/user-roles/{id}/{id}", USER_ROLES, DELETE),
            new PermissionSeed("GET", "/api/role-permissions/roles/{id}/matrix", ROLE_PERMISSIONS, VIEW),
            new PermissionSeed("GET", "/api/role-permissions/roles/{id}", ROLE_PERMISSIONS, LIST),
            new PermissionSeed("PUT", "/api/role-permissions/roles/{id}", ROLE_PERMISSIONS, UPDATE),
            new PermissionSeed("POST", "/api/role-permissions", ROLE_PERMISSIONS, CREATE),
            new PermissionSeed("DELETE", "/api/role-permissions/{id}/{id}", ROLE_PERMISSIONS, DELETE),
            new PermissionSeed("GET", "/api/audit/login-attempts", AUDIT, LIST),
            new PermissionSeed("GET", "/api/permissions", PERMISSIONS, LIST),
            new PermissionSeed("POST", "/api/permissions", PERMISSIONS, CREATE),
            new PermissionSeed("GET", "/api/permissions/{id}", PERMISSIONS, VIEW),
            new PermissionSeed("PUT", "/api/permissions/{id}", PERMISSIONS, UPDATE),
            new PermissionSeed("DELETE", "/api/permissions/{id}", PERMISSIONS, DELETE)
    };

    // Recursos de los demás módulos del mockup de HU-SE-06. Sus microservicios
    // llegan en entregas posteriores: las URLs son tentativas (CRUD REST) y se
    // corrigen desde la pantalla Permisos cuando existan.
    private static final List<PermissionSeed> MODULE_PERMISSIONS = Stream.of(
            crud(PermissionModule.SPORTS_MANAGEMENT, "Torneos", "/api/tournaments"),
            crud(PermissionModule.SPORTS_MANAGEMENT, "Equipos", "/api/teams"),
            crud(PermissionModule.SPORTS_MANAGEMENT, "Jugadores", "/api/players"),
            crud(PermissionModule.SPORTS_MANAGEMENT, "Partidos", "/api/matches"),
            crud(PermissionModule.COMMERCIAL, "Entradas", "/api/tickets"),
            crud(PermissionModule.INFRASTRUCTURE, "Escenarios", "/api/venues"),
            crud(PermissionModule.REPORTS, "Reportes", "/api/reports")
    ).flatMap(List::stream).toList();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.seed-admin.email:admin@uc.edu.co}")
    private String adminEmail;

    @Value("${security.seed-admin.password:}")
    private String adminPassword;

    @Bean
    CommandLineRunner seedDatabase() {
        return arguments -> {
            boolean adminRoleIsNew = isNewRole("ADMIN");
            Role adminRole = findOrCreateRole("ADMIN", "Administrador");
            boolean consultantRoleIsNew = isNewRole("CONSULTOR");
            Role consultantRole = findOrCreateRole(
                    "CONSULTOR",
                    "Consulta de usuarios"
            );

            if (!adminPassword.isBlank()) {
                User adminUser = findOrCreateUser();
                assignRole(adminUser, adminRole);
            }

            // Un permiso se asigna al administrador solo cuando se crea el rol
            // o el permiso: si después lo retira en la matriz (HU-SE-06), el
            // cambio se conserva al reiniciar el backend.
            for (PermissionSeed seed : ADMIN_PERMISSIONS) {
                boolean permissionIsNew = isNewPermission(seed);
                Permission permission = findOrCreatePermission(seed);
                if (adminRoleIsNew || permissionIsNew) {
                    assignPermission(adminRole, permission);
                }
            }

            // Solo se siembra una casilla (recurso + operación) vacía, y se
            // asigna al administrador una única vez: si después la retira o
            // cambia la URL, no vuelve a aparecer al reiniciar.
            for (PermissionSeed seed : MODULE_PERMISSIONS) {
                if (isCellEmpty(seed)) {
                    assignPermission(adminRole, findOrCreatePermission(seed));
                }
            }

            // Permisos iniciales del consultor: solo al crear el rol
            if (consultantRoleIsNew) {
                assignPermission(
                        consultantRole,
                        findOrCreatePermission(new PermissionSeed("GET", "/api/users", USERS, LIST))
                );
                assignPermission(
                        consultantRole,
                        findOrCreatePermission(new PermissionSeed("GET", "/api/users/{id}", USERS, VIEW))
                );
            }
        };
    }

    private boolean isNewRole(String name) {
        return roleRepository.findByNameIgnoreCase(name).isEmpty();
    }

    private boolean isNewPermission(PermissionSeed seed) {
        return !permissionRepository.existsByUrlAndMethod(
                seed.url(), seed.method().toUpperCase(Locale.ROOT));
    }

    private Role findOrCreateRole(String name, String description) {
        return roleRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    role.setDescription(description);
                    return roleRepository.save(role);
                });
    }

    private User findOrCreateUser() {
        String email = adminEmail.trim();
        if (email.isEmpty()) {
            throw new IllegalStateException(
                    "security.seed-admin.email no puede estar vacío"
            );
        }
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = new User();
                    user.setName("Administrador");
                    user.setEmail(email);
                    user.setPassword(passwordEncoder.encode(adminPassword));
                    return userRepository.save(user);
                });
    }

    // Crea el permiso si no existe; si existe sin clasificar (versiones
    // anteriores), le asigna módulo, recurso y operación.
    private Permission findOrCreatePermission(PermissionSeed seed) {
        String method = seed.method().toUpperCase(Locale.ROOT);
        Permission permission = permissionRepository.findByUrlAndMethod(seed.url(), method)
                .orElseGet(() -> {
                    Permission newPermission = new Permission();
                    newPermission.setUrl(seed.url());
                    newPermission.setMethod(method);
                    return newPermission;
                });
        if (permission.getId() == null || permission.getModule() == null) {
            permission.setModule(seed.module());
            permission.setResource(seed.resource());
            permission.setOperation(seed.operation());
            permission = permissionRepository.save(permission);
        }
        return permission;
    }

    private boolean isCellEmpty(PermissionSeed seed) {
        return !permissionRepository.existsByModuleAndResourceAndOperation(
                seed.module(), seed.resource(), seed.operation());
    }

    // Los 5 permisos de un recurso: Ver, Listar, Crear, Actualizar y Eliminar
    private static List<PermissionSeed> crud(
            PermissionModule module,
            String resource,
            String baseUrl) {
        String byId = baseUrl + "/{id}";
        return List.of(
                new PermissionSeed(module, "GET", byId, resource, VIEW),
                new PermissionSeed(module, "GET", baseUrl, resource, LIST),
                new PermissionSeed(module, "POST", baseUrl, resource, CREATE),
                new PermissionSeed(module, "PUT", byId, resource, UPDATE),
                new PermissionSeed(module, "DELETE", byId, resource, DELETE)
        );
    }

    private void assignRole(User user, Role role) {
        if (!userRoleRepository.existsByUserIdAndRoleId(user.getId(), role.getId())) {
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        }
    }

    private void assignPermission(Role role, Permission permission) {
        if (!rolePermissionRepository.existsByRoleIdAndPermissionId(
                role.getId(), permission.getId())) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole(role);
            rolePermission.setPermission(permission);
            rolePermissionRepository.save(rolePermission);
        }
    }

    private record PermissionSeed(
            PermissionModule module,
            String method,
            String url,
            String resource,
            PermissionOperation operation) {

        // Permisos de este microservicio (módulo Administración)
        PermissionSeed(String method, String url, String resource, PermissionOperation operation) {
            this(PermissionModule.ADMINISTRATION, method, url, resource, operation);
        }
    }
}
