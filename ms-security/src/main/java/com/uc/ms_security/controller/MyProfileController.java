package com.uc.ms_security.controller;

import com.uc.ms_security.dto.profile.ProfilePhotoDTO;
import com.uc.ms_security.dto.profile.ProfileRequestDTO;
import com.uc.ms_security.dto.profile.ProfileResponseDTO;
import com.uc.ms_security.dto.user.UserDetailResponseDTO;
import com.uc.ms_security.service.IProfilePhotoService;
import com.uc.ms_security.service.IProfileService;
import com.uc.ms_security.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// "Mi perfil": el usuario autenticado gestiona únicamente su propio perfil.
// El id se toma del token (claim sub), nunca de la URL.
@RestController
@RequestMapping("/api/profile/me")
@RequiredArgsConstructor
public class MyProfileController {

    private final IUserService userService;
    private final IProfileService profileService;
    private final IProfilePhotoService profilePhotoService;

    @GetMapping
    public UserDetailResponseDTO findMine(@AuthenticationPrincipal Jwt jwt) {
        return userService.findByIdWithProfile(currentUserId(jwt));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponseDTO create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ProfileRequestDTO dto) {
        return profileService.create(currentUserId(jwt), dto);
    }

    @PutMapping
    public ProfileResponseDTO update(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ProfileRequestDTO dto) {
        return profileService.update(currentUserId(jwt), dto);
    }

    @PutMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfileResponseDTO uploadPhoto(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("file") MultipartFile file) {
        return profilePhotoService.upload(currentUserId(jwt), file);
    }

    @GetMapping("/photo")
    public ResponseEntity<byte[]> downloadPhoto(@AuthenticationPrincipal Jwt jwt) {
        ProfilePhotoDTO photo = profilePhotoService.download(currentUserId(jwt));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.contentType()))
                .body(photo.content());
    }

    @DeleteMapping("/photo")
    public ProfileResponseDTO removePhoto(@AuthenticationPrincipal Jwt jwt) {
        return profilePhotoService.remove(currentUserId(jwt));
    }

    private Long currentUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
