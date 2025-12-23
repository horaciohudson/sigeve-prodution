package br.com.sigeve.sigeve_prodution.controller;

import br.com.sigeve.sigeve_prodution.dto.CreateTenantRequestDTO;
import br.com.sigeve.sigeve_prodution.dto.TenantDTO;
import br.com.sigeve.sigeve_prodution.dto.UpdateTenantRequestDTO;
import br.com.sigeve.sigeve_prodution.service.TenantService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TenantController {

    private final TenantService tenantService;
    private static final Logger log = LoggerFactory.getLogger(TenantController.class);
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /**
     * Lista todos os tenants ativos
     */
    @GetMapping
    public ResponseEntity<List<TenantDTO>> getAllTenants() {
        try {
            log.info("Listando todos os tenants ativos");
            
            List<TenantDTO> tenants = tenantService.findAllActive();
            return ResponseEntity.ok(tenants);
        } catch (Exception e) {
            log.error("Erro ao listar tenants", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca tenant por ID
     */
    @GetMapping("/{tenantId}")
    public ResponseEntity<TenantDTO> getTenantById(@PathVariable UUID tenantId) {
        try {
            log.info("Buscando tenant por ID: {}", tenantId);
            
            Optional<TenantDTO> tenant = tenantService.findById(tenantId);
            return tenant.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar tenant por ID: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca tenant por código
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<TenantDTO> getTenantByCode(@PathVariable String code) {
        try {
            log.info("Buscando tenant por código: {}", code);
            
            Optional<TenantDTO> tenant = tenantService.findByCode(code);
            return tenant.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Erro ao buscar tenant por código: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cria novo tenant
     */
    @PostMapping
    public ResponseEntity<TenantDTO> createTenant(
            @Valid @RequestBody CreateTenantRequestDTO createRequest,
            HttpServletRequest request) {
        try {
            String username = extractUsernameFromToken(request);
            
            log.info("Criando novo tenant: {} por usuário: {}", createRequest.getCode(), username);
            
            TenantDTO createdTenant = tenantService.createTenant(createRequest, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTenant);
        } catch (RuntimeException e) {
            log.error("Erro de validação ao criar tenant: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao criar tenant", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Atualiza tenant existente
     */
    @PutMapping("/{tenantId}")
    public ResponseEntity<TenantDTO> updateTenant(
            @PathVariable UUID tenantId,
            @Valid @RequestBody UpdateTenantRequestDTO updateRequest,
            HttpServletRequest request) {
        try {
            String username = extractUsernameFromToken(request);
            
            log.info("Atualizando tenant: {} por usuário: {}", tenantId, username);
            
            TenantDTO updatedTenant = tenantService.updateTenant(tenantId, updateRequest, username);
            return ResponseEntity.ok(updatedTenant);
        } catch (RuntimeException e) {
            log.error("Erro de validação ao atualizar tenant: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao atualizar tenant: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Desativa tenant (bloqueio ao invés de exclusão)
     */
    @DeleteMapping("/{tenantId}")
    public ResponseEntity<Void> deactivateTenant(
            @PathVariable UUID tenantId,
            HttpServletRequest request) {
        try {
            String username = extractUsernameFromToken(request);
            
            log.info("Desativando tenant: {} por usuário: {}", tenantId, username);
            
            tenantService.deactivateTenant(tenantId, username);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erro de validação ao desativar tenant: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro ao desativar tenant: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Extrai username do token JWT
     */
    private String extractUsernameFromToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        Claims claims = Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Extrai token do cabeçalho Authorization
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Token JWT não encontrado");
    }
}