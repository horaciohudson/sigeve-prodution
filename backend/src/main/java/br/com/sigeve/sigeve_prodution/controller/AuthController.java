package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.LoginRequestDTO;
import br.com.sigeve.sigeve_prodution.dto.LoginResponseDTO;
import br.com.sigeve.sigeve_prodution.model.Tenant;
import br.com.sigeve.sigeve_prodution.model.User;
import br.com.sigeve.sigeve_prodution.repository.TenantRepository;
import br.com.sigeve.sigeve_prodution.security.CustomUserDetailsService;
import br.com.sigeve.sigeve_prodution.security.JwtService;
import br.com.sigeve.sigeve_prodution.service.AccountLockoutService;
import br.com.sigeve.sigeve_prodution.service.LoginLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final CustomUserDetailsService users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final TenantRepository tenantRepository;
    private final LoginLogService loginLogService;
    private final AccountLockoutService accountLockoutService;

    public AuthController(CustomUserDetailsService users, PasswordEncoder encoder, JwtService jwt, TenantRepository tenantRepository, LoginLogService loginLogService, AccountLockoutService accountLockoutService) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
        this.tenantRepository = tenantRepository;
        this.loginLogService = loginLogService;
        this.accountLockoutService = accountLockoutService;
    }

    private UUID resolveTenantId(String tenantCode) {
        Tenant tenant = tenantRepository.findByCode(tenantCode)
                .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantCode));
        return tenant.getId();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO req, HttpServletRequest request) {
        UUID tenantId = null;
        User u = null;
        
        try {
            tenantId = resolveTenantId(req.tenantCode());
            
            // ✅ Agora pega sua entidade User corretamente
            u = users.loadByUsernameAndTenant(req.username(), tenantId);
            users.assertActive(u);

            // Check if account is locked
            if (accountLockoutService.isAccountLocked(u)) {
                loginLogService.logLoginFailure(tenantId, req.username(), "Account locked", request);
                return ResponseEntity.status(423).build(); // 423 Locked
            }

            // ✅ Usa passwordHash da entidade
            if (!encoder.matches(req.password(), u.getPasswordHash())) {
                accountLockoutService.recordFailedAttempt(u.getId());
                loginLogService.logLoginFailure(tenantId, req.username(), "Invalid password", request);
                return ResponseEntity.status(401).build();
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("user_id", u.getId().toString());
            claims.put("username", u.getUsername());
            claims.put("tenant_id", u.getTenantId().toString());
            claims.put("roles", u.getRoles().stream().map(r -> r.getRole().name()).toList());

            String access = jwt.generate(claims);
            String refresh = jwt.generateRefresh(claims);

            // Reset failed attempts on successful login
            accountLockoutService.resetFailedAttempts(u.getId());
            
            // Log successful login
            loginLogService.logLoginSuccess(tenantId, u.getId(), u.getUsername(), request);

            return ResponseEntity.ok(new LoginResponseDTO(access, refresh, "Bearer", 3600));
            
        } catch (Exception e) {
            // Log failed login attempt
            String failureReason = e.getMessage();
            if (tenantId == null) {
                // Try to resolve tenant for logging purposes, but don't fail if it doesn't exist
                try {
                    tenantId = resolveTenantId(req.tenantCode());
                } catch (Exception ignored) {
                    // Use null tenant if tenant resolution fails
                }
            }
            loginLogService.logLoginFailure(tenantId, req.username(), failureReason, request);
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        var body = jwt.parse(token).getBody();
        return ResponseEntity.ok(Map.of(
                "user_id", body.get("user_id"),
                "username", body.get("username"),
                "tenant_id", body.get("tenant_id"),
                "roles", body.get("roles")
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        var claims = jwt.parse(token).getBody();

        String access = jwt.generate(claims);
        String refresh = jwt.generateRefresh(claims);
        return ResponseEntity.ok(new LoginResponseDTO(access, refresh, "Bearer", 3600));
    }
}