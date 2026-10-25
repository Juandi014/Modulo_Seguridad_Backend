package com.uc.ms_security.service.impl;

import com.uc.ms_security.dto.profile.ProfilePhotoDTO;
import com.uc.ms_security.dto.profile.ProfileResponseDTO;
import com.uc.ms_security.entity.Profile;
import com.uc.ms_security.exception.ApplicationException;
import com.uc.ms_security.exception.ErrorCase;
import com.uc.ms_security.mapper.ProfileMapper;
import com.uc.ms_security.repository.ProfileRepository;
import com.uc.ms_security.service.IFileStorageService;
import com.uc.ms_security.service.IProfilePhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// Reglas de negocio de la fotografía del perfil: solo JPG o PNG, máximo 2 MB.
@Service
@RequiredArgsConstructor
public class ProfilePhotoServiceImpl implements IProfilePhotoService {

    private static final long MAX_SIZE_BYTES = 2L * 1024 * 1024;

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final IFileStorageService fileStorageService;

    @Override
    public ProfileResponseDTO upload(Long userId, MultipartFile file) {
        Profile profile = findProfile(userId);
        byte[] content = readValidImage(file);
        String extension = detectExtension(content);

        String newFilename = fileStorageService.store(content, extension);
        String oldFilename = profile.getPhotoFilename();
        profile.setPhotoFilename(newFilename);
        Profile savedProfile = profileRepository.save(profile);

        if (oldFilename != null) {
            fileStorageService.delete(oldFilename);
        }
        return profileMapper.toResponseDTO(savedProfile);
    }

    @Override
    public ProfilePhotoDTO download(Long userId) {
        Profile profile = findProfile(userId);
        String filename = profile.getPhotoFilename();
        if (filename == null) {
            throw new ApplicationException(
                    ErrorCase.NOT_FOUND,
                    "El usuario no tiene fotografía"
            );
        }
        String contentType = filename.endsWith(".png") ? "image/png" : "image/jpeg";
        return new ProfilePhotoDTO(fileStorageService.load(filename), contentType);
    }

    @Override
    public ProfileResponseDTO remove(Long userId) {
        Profile profile = findProfile(userId);
        String filename = profile.getPhotoFilename();
        if (filename == null) {
            throw new ApplicationException(
                    ErrorCase.NOT_FOUND,
                    "El usuario no tiene fotografía"
            );
        }
        profile.setPhotoFilename(null);
        Profile savedProfile = profileRepository.save(profile);
        fileStorageService.delete(filename);
        return profileMapper.toResponseDTO(savedProfile);
    }

    private Profile findProfile(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ApplicationException(
                        ErrorCase.NOT_FOUND,
                        "Primero debe registrar el perfil del usuario"
                ));
    }

    private byte[] readValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApplicationException(
                    ErrorCase.INVALID_OPERATION,
                    "Debe seleccionar una imagen"
            );
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new ApplicationException(
                    ErrorCase.INVALID_OPERATION,
                    "La imagen no puede superar 2 MB"
            );
        }
        try {
            return file.getBytes();
        } catch (IOException exception) {
            throw new ApplicationException(
                    ErrorCase.INVALID_OPERATION,
                    "No se pudo leer la imagen"
            );
        }
    }

    // Se revisan los primeros bytes del archivo (no la extensión ni el
    // Content-Type, que el cliente puede falsificar).
    private String detectExtension(byte[] content) {
        if (content.length >= 3
                && (content[0] & 0xFF) == 0xFF
                && (content[1] & 0xFF) == 0xD8
                && (content[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (content.length >= 8
                && (content[0] & 0xFF) == 0x89
                && content[1] == 'P'
                && content[2] == 'N'
                && content[3] == 'G') {
            return "png";
        }
        throw new ApplicationException(
                ErrorCase.INVALID_OPERATION,
                "Solo se permiten imágenes JPG o PNG"
        );
    }
}
