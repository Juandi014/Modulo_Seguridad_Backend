package com.uc.ms_security.repository;

import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
//En un join debe existir oblligatoriamente un dato en común en la otra tabla

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<User> findByEmail(String email);

    boolean existsByIdAndActiveTrue(Long id);

    // Usuario OAuth ya creado en un acceso anterior con el mismo proveedor
    Optional<User> findByAuthProviderAndProviderId(AuthProvider authProvider, String providerId);

    // Usuarios disponibles para un rol: los que todavía no lo tienen asignado
    @Query("""
            SELECT u
            FROM User u
            WHERE NOT EXISTS (
                SELECT ur FROM UserRole ur
                WHERE ur.user = u AND ur.role.id = :roleId
            )
            ORDER BY u.name
            """)
    List<User> findAllWithoutRole(@Param("roleId") Long roleId);

    @EntityGraph(attributePaths = {"profile"})
    Optional<User> findWithProfileById(Long id);

    @EntityGraph(attributePaths = {"sessions"})
    Optional<User> findWithSessionsById(Long id);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<User> findWithRolesById(Long id);
}
