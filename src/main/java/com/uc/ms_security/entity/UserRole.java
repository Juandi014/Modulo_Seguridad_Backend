package com.uc.ms_security.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "user_role",
        uniqueConstraints = { 
                @UniqueConstraint(
                        name = "uk_user_role",
                        columnNames = {"user_id", "role_id"} //Es una constraint compuesta que asegura que no puede haber duplicados de la combinación de user_id y role_id en la tabla user_role.
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne( //Tiene una referencia a la entidad User, y cada instancia de UserRole está asociada a un único usuario.
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id", //user_id es la clave foránea que hace referencia a la tabla users.
            nullable = false
    )
    private User user;

    @ManyToOne( //Tiene una referencia a la entidad Role, y cada instancia de UserRole está asociada a un único rol.
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "role_id", //role_id es la clave foránea que hace referencia a la tabla roles.
            nullable = false
    )
    private Role role; 
}