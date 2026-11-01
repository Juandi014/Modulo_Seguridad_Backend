package com.uc.ms_security.dto.profile;

// Contenido binario de la fotografía y su tipo (image/jpeg o image/png)
public record ProfilePhotoDTO(
        byte[] content,
        String contentType) {
}
