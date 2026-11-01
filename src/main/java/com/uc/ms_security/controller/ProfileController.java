package com.uc.ms_security.controller;

import com.uc.ms_security.dto.profile.ProfilePhotoDTO;
import com.uc.ms_security.dto.profile.ProfileRequestDTO;
import com.uc.ms_security.dto.profile.ProfileResponseDTO;
import com.uc.ms_security.service.IProfilePhotoService;
import com.uc.ms_security.service.IProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// Gestión del perfil de cualquier usuario (administrador).
// La consulta del usuario con su perfil está en UserController: GET /api/users/{id}/profile
@RestController
@RequestMapping("/api/users/{userId}/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final IProfileService profileService;
    private final IProfilePhotoService profilePhotoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponseDTO create(
            @PathVariable Long userId,
            @Valid @RequestBody ProfileRequestDTO dto) {
        return profileService.create(userId, dto);
    }

    @PutMapping
    public ProfileResponseDTO update(
            @PathVariable Long userId,
            @Valid @RequestBody ProfileRequestDTO dto) {
        return profileService.update(userId, dto);
    }

    @PutMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfileResponseDTO uploadPhoto(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        return profilePhotoService.upload(userId, file);
    }

    @GetMapping("/photo")
    public ResponseEntity<byte[]> downloadPhoto(@PathVariable Long userId) {
        ProfilePhotoDTO photo = profilePhotoService.download(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.contentType()))
                .body(photo.content());
    }

    @DeleteMapping("/photo")
    public ProfileResponseDTO removePhoto(@PathVariable Long userId) {
        return profilePhotoService.remove(userId);
    }
}
