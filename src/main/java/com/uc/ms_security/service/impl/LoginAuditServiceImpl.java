package com.uc.ms_security.service.impl;

import com.uc.ms_security.dto.common.PageResponseDTO;
import com.uc.ms_security.dto.audit.LoginAttemptResponseDTO;
import com.uc.ms_security.entity.enums.AuthProvider;
import com.uc.ms_security.entity.LoginAttempt;
import com.uc.ms_security.entity.enums.LoginAttemptResult;
import com.uc.ms_security.entity.User;
import com.uc.ms_security.mapper.LoginAttemptMapper;
import com.uc.ms_security.repository.LoginAttemptRepository;
import com.uc.ms_security.service.ILoginAuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginAuditServiceImpl implements ILoginAuditService {

    private static final int MAX_PAGE_SIZE = 100;

    private final LoginAttemptRepository loginAttemptRepository;
    private final LoginAttemptMapper loginAttemptMapper;

    // REQUIRES_NEW: el registro se guarda en su propia transacción, así queda
    // aunque la operación que lo llama falle y deshaga sus cambios.
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String email, User user, AuthProvider method, LoginAttemptResult result) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setEmail(email != null ? truncate(email.trim(), 150) : null);
        attempt.setUser(user);
        attempt.setMethod(method);
        attempt.setResult(result);
        attempt.setIpAddress(clientIp());
        attempt.setCreatedAt(LocalDateTime.now());
        loginAttemptRepository.save(attempt);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<LoginAttemptResponseDTO> search(
            String email,
            LoginAttemptResult result,
            int page,
            int size) {
        PageRequest pageRequest = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        String emailFilter = (email == null || email.isBlank()) ? null : email.trim();
        return loginAttemptMapper.toPageResponseDTO(
                loginAttemptRepository.search(emailFilter, result, pageRequest)
        );
    }

    // IP de la petición en curso (si hay un proxy, la primera de X-Forwarded-For)
    private String clientIp() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        String forwarded = request.getHeader("X-Forwarded-For");
        String ip = (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
        return truncate(ip, 45);
    }

    private String truncate(String value, int max) {
        return value != null && value.length() > max ? value.substring(0, max) : value;
    }
}
