package com.uc.ms_security.repository;

import com.uc.ms_security.entity.LoginAttempt;
import com.uc.ms_security.entity.enums.LoginAttemptResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    // Filtros opcionales: correo (contiene) y resultado; los nulos se ignoran
    @EntityGraph(attributePaths = {"user"})
    @Query("""
            SELECT a
            FROM LoginAttempt a
            WHERE (:email IS NULL OR LOWER(a.email) LIKE LOWER(CONCAT('%', :email, '%')))
              AND (:result IS NULL OR a.result = :result)
            """)
    Page<LoginAttempt> search(
            @Param("email") String email,
            @Param("result") LoginAttemptResult result,
            Pageable pageable
    );
}
