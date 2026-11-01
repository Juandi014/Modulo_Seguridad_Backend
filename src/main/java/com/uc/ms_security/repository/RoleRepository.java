package com.uc.ms_security.repository;

//Todo lo que tiene que ver con consultas SQL

import com.uc.ms_security.entity.Role;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    Optional<Role> findByNameIgnoreCase(String name);

    @EntityGraph(attributePaths = {"rolePermissions", "rolePermissions.permission"})
    Optional<Role> findWithPermissionsById(Long id);

    // Roles disponibles para un usuario: los que todavía no tiene asignados
    @Query("""
            SELECT r
            FROM Role r
            WHERE NOT EXISTS (
                SELECT ur FROM UserRole ur
                WHERE ur.role = r AND ur.user.id = :userId
            )
            ORDER BY r.name
            """)
    List<Role> findAllNotAssignedToUser(@Param("userId") Long userId);
}