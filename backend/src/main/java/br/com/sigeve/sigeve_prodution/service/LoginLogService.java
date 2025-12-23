package br.com.sigeve.sigeve_prodution.service;

import br.com.sigeve.sigeve_prodution.enums.LoginEventType;
import br.com.sigeve.sigeve_prodution.model.LoginLog;
import br.com.sigeve.sigeve_prodution.repository.LoginLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LoginLogService {

    private final LoginLogRepository loginLogRepository;

    public LoginLogService(LoginLogRepository loginLogRepository) {
        this.loginLogRepository = loginLogRepository;
    }

    public void logLoginSuccess(UUID tenantId, UUID userId, String username, HttpServletRequest request) {
        LoginLog log = new LoginLog();
        log.setTenantId(tenantId);
        log.setUserId(userId);
        log.setUsername(username);
        log.setEventType(LoginEventType.LOGIN_SUCCESS);
        log.setIpAddress(getClientIpAddress(request));
        log.setUserAgent(request.getHeader("User-Agent"));
        
        loginLogRepository.save(log);
    }

    public void logLoginFailure(UUID tenantId, String username, String failureReason, HttpServletRequest request) {
        LoginLog log = new LoginLog();
        log.setTenantId(tenantId);
        log.setUsername(username);
        log.setEventType(LoginEventType.LOGIN_FAIL);
        log.setFailureReason(failureReason);
        log.setIpAddress(getClientIpAddress(request));
        log.setUserAgent(request.getHeader("User-Agent"));
        
        loginLogRepository.save(log);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}