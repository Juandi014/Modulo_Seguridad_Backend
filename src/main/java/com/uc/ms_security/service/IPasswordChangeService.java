package com.uc.ms_security.service;

// Cambio de contraseña del usuario autenticado (conoce la actual).
public interface IPasswordChangeService {

    void changePassword(Long userId, String currentPassword, String newPassword);
}
