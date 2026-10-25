package com.uc.ms_security.service.impl;

import com.uc.ms_security.authorization.UrlPatternNormalizer;
import com.uc.ms_security.repository.PermissionRepository;
import com.uc.ms_security.service.IAccessControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessControlServiceImpl implements IAccessControlService {

    private final PermissionRepository permissionRepository;
    private final UrlPatternNormalizer urlPatternNormalizer;

    @Override
    public boolean hasPermission(Long userId, String httpMethod, String requestUrl) {
        return !permissionRepository.findByUserIdAndMethodAndUrl(
                userId,
                urlPatternNormalizer.normalizeMethod(httpMethod),
                urlPatternNormalizer.normalizeUrl(requestUrl)
        ).isEmpty();
    }
}
