package com.uc.ms_security.repository;

import com.uc.ms_security.entity.Permission;
import com.uc.ms_security.entity.enums.PermissionModule;
import com.uc.ms_security.entity.enums.PermissionOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    boolean existsByUrlAndMethod(String url, String method);

    boolean existsByUrlAndMethodAndIdNot(String url, String method, Long id);

    boolean existsByModuleAndResourceAndOperation(
            PermissionModule module,
            String resource,
            PermissionOperation operation
    );

    @Query("""
            SELECT DISTINCT p
            FROM Permission p
            JOIN p.rolePermissions rp
            JOIN rp.role r
            JOIN r.userRoles ur
            WHERE ur.user.id = :userId
              AND ur.user.active = true
              AND r.active = true
              AND UPPER(p.method) = UPPER(:method)
              AND p.url = :url
            """)
    List<Permission> findByUserIdAndMethodAndUrl(
            @Param("userId") Long userId,
            @Param("method") String method,
            @Param("url") String url
    );

    Optional<Permission> findByUrlAndMethod(String url, String method);
}
