package com.uc.ms_security.mapper;

import com.uc.ms_security.dto.common.PageResponseDTO;
import com.uc.ms_security.dto.audit.LoginAttemptResponseDTO;
import com.uc.ms_security.entity.LoginAttempt;
import com.uc.ms_security.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class LoginAttemptMapper {

    public LoginAttemptResponseDTO toResponseDTO(LoginAttempt attempt) {
        User user = attempt.getUser();
        return new LoginAttemptResponseDTO(
                attempt.getId(),
                attempt.getEmail(),
                user != null ? user.getId() : null,
                user != null ? user.getName() : null,
                attempt.getMethod(),
                attempt.getResult(),
                attempt.getIpAddress(),
                attempt.getCreatedAt()
        );
    }

    public PageResponseDTO<LoginAttemptResponseDTO> toPageResponseDTO(Page<LoginAttempt> page) {
        return new PageResponseDTO<>(
                page.getContent().stream().map(this::toResponseDTO).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
