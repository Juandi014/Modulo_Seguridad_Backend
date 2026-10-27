package com.uc.ms_security.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
public class Profile {

    @Id
        @GeneratedValue(
            strategy = GenerationType.IDENTITY
        )
    private Long id;

        @Column(
            length = 30
        )
    private String phone;

        // Solo se guarda el nombre del archivo; la imagen vive en disco
        // (carpeta configurada en storage.profile-photos.dir).
        @Column(
            name = "photo_filename",
            length = 100
        )
    private String photoFilename;

        @OneToOne(
            fetch = FetchType.LAZY
        )
        @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
        )
    private User user;
}
