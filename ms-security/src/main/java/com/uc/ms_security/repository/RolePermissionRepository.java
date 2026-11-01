package com.uc.ms_security.repository;

import com.uc.ms_security.entity.RolePermission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);

    @EntityGraph(attributePaths = {"role", "permission"})
    List<RolePermission> findAllByRoleId(Long roleId);

    @EntityGraph(attributePaths = {"role", "permission"})
    List<RolePermission> findAllByPermissionId(Long permissionId);

    @Override
    @EntityGraph(attributePaths = {"role", "permission"})
    List<RolePermission> findAll();

    @EntityGraph(attributePaths = {"role", "permission"})
    Optional<RolePermission> findByRoleIdAndPermissionId(Long roleId, Long permissionId);

    boolean existsByPermissionId(Long permissionId);

    long countByPermissionId(Long permissionId);

    // ¿El usuario tiene el permiso a través de algún rol activo distinto de roleId?
    @Query("""
            SELECT COUNT(rp) > 0
            FROM RolePermission rp
            JOIN rp.role r
            JOIN r.userRoles ur
            WHERE ur.user.id = :userId
              AND rp.permission.id = :permissionId
              AND r.id <> :roleId
              AND r.active = true
            """)
    boolean userHasPermissionThroughOtherRole(
            @Param("userId") Long userId,
            @Param("permissionId") Long permissionId,
            @Param("roleId") Long roleId
    );
}
